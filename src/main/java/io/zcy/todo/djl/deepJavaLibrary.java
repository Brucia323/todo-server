package io.zcy.todo.djl;

import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.nn.recurrent.LSTM;
import ai.djl.tablesaw.TablesawDataset;
import ai.djl.translate.TranslateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zcy.todo.todo.record.TodoRecord;
import io.zcy.todo.todo.record.TodoRecordService;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.tablesaw.io.json.JsonReadOptions;

@Component
@Slf4j
public class deepJavaLibrary {
  @Resource private TodoRecordService todoRecordService;

  public void lstm(Integer userId) throws IOException, TranslateException {
    List<TodoRecord> todoRecords =
        todoRecordService
            .getTodoRecordsByUserId(userId)
            .groupBy(todoRecord -> todoRecord.getCreateTime().toLocalDate())
            .flatMap(
                group ->
                    group.reduce(
                        (r1, r2) ->
                            new TodoRecord(r1.getAmount() + r2.getAmount(), r1.getCreateTime())))
            .sort(Comparator.comparing(a -> a.getCreateTime().toLocalDate()))
            .collectList()
            .block();
    if (todoRecords == null) {
      throw new RuntimeException("暂无数据");
    }
    ObjectMapper mapper = new ObjectMapper();
    List<Map<String, Object>> nodes = new ArrayList<>();
    todoRecords.forEach(
        todoRecord -> {
          Map<String, Object> node = new HashMap<>();
          node.put("date", todoRecord.getCreateTime().toLocalDate());
          node.put("amount", todoRecord.getAmount());
          nodes.add(node);
        });
    String json = mapper.writeValueAsString(nodes);
    TablesawDataset dataset =
        TablesawDataset.builder()
            .setReadOptions(JsonReadOptions.builderFromString(json).build())
            .build();
    dataset.prepare();
  }

  private static Block getLSTMModel() {
    SequentialBlock block = new SequentialBlock();
    block.addSingleton(
        input -> {
          Shape inputShape = input.getShape();
          long batchSize = inputShape.get(0);
          long channel = inputShape.get(3);
          long time = inputShape.size() / (batchSize * channel);
          return input.reshape(new Shape(batchSize, time, channel));
        });
    block.add(
        new LSTM.Builder()
            .setStateSize(64)
            .setNumLayers(1)
            .optDropRate(0)
            .optReturnState(false)
            .build());
    block.add(BatchNorm.builder().optEpsilon(1e-5f).optMomentum(0.9f).build());
    block.add(Blocks.batchFlattenBlock());
    block.add(Linear.builder().setUnits(10).build());
    return block;
  }
}
