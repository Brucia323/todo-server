package io.zcy.todo.djl;

import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.nn.recurrent.LSTM;
import ai.djl.tablesaw.TablesawDataset;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingResult;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.SaveModelTrainingListener;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.util.ProgressBar;
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
public class DeepJavaLibrary {
  @Resource private TodoRecordService todoRecordService;

  public TrainingResult lstm(Integer userId) throws IOException, TranslateException {
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
          node.put("date", todoRecord.getCreateTime().toLocalDate().toString());
          node.put("amount", todoRecord.getAmount());
          nodes.add(node);
        });
    String json = mapper.writeValueAsString(nodes);
    log.info("元数据: {}", json);
    TablesawDataset dataset =
        TablesawDataset.builder()
            .setReadOptions(JsonReadOptions.builderFromString(json).build())
            .addNumericFeature("amount")
            .addNumericLabel("date")
            .setSampling(2, false)
            .build();
    dataset.prepare(new ProgressBar());
    log.info("数据集大小: {}", dataset.size());
    try (Model model = Model.newInstance("lstm")) {
      model.setBlock(getLSTMModel());
      DefaultTrainingConfig config = setupTrainingConfig();
      try (Trainer trainer = model.newTrainer(config)) {
        trainer.setMetrics(new Metrics());
        Shape shape = new Shape(32, 1, dataset.size(), 2);
        trainer.initialize(shape);
        EasyTrain.fit(trainer, 1, dataset, dataset);
        return trainer.getTrainingResult();
      }
    }
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

  private static DefaultTrainingConfig setupTrainingConfig() {
    String outputDir = "/build/model";
    SaveModelTrainingListener listener = new SaveModelTrainingListener(outputDir);
    listener.setSaveModelCallback(
        trainer -> {
          TrainingResult result = trainer.getTrainingResult();
          Model model = trainer.getModel();
          float accuracy = result.getValidateEvaluation("Accuracy");
          model.setProperty("Accuracy", String.format("%.5f", accuracy));
          model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
        });

    return new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
        .addEvaluator(new Accuracy())
        .optDevices(Engine.getInstance().getDevices(1))
        .addTrainingListeners(TrainingListener.Defaults.logging(outputDir))
        .addTrainingListeners(listener);
  }
}
