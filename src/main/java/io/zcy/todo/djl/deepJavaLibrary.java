package io.zcy.todo.djl;

import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.nn.recurrent.LSTM;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.ArrayDataset;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.SaveModelTrainingListener;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import io.zcy.todo.todo.record.TodoRecord;
import io.zcy.todo.todo.record.TodoRecordService;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    List<LocalDate> dates =
        todoRecords.stream().map(todoRecord -> todoRecord.getCreateTime().toLocalDate()).toList();
    int[] amounts =
        todoRecords.stream().map(TodoRecord::getAmount).mapToInt(Integer::valueOf).toArray();
    try (NDManager manager = NDManager.newBaseManager()) {
      NDArray labels = manager.arange(amounts.length);
      NDArray data = manager.create(amounts);
      ArrayDataset arrayDataset =
          new ArrayDataset.Builder().optLabels(labels).setData(data).setSampling(1, false).build();
      arrayDataset.prepare(new ProgressBar());
      try (Model model = Model.newInstance("lstm")) {
        model.setBlock(getLSTMModel());
        DefaultTrainingConfig config = setupTrainingConfig();
        try (Trainer trainer = model.newTrainer(config)) {
          trainer.setMetrics(new Metrics());
          Shape shape = new Shape(1, arrayDataset.size(), 1);
          trainer.initialize(shape);
          EasyTrain.fit(trainer, 1, arrayDataset, arrayDataset);
          return trainer.getTrainingResult();
        }
      }
    }
  }

  private static Block getLSTMModel() {
    SequentialBlock block = new SequentialBlock();
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

    return new DefaultTrainingConfig(Loss.maskedSoftmaxCrossEntropyLoss())
        .addEvaluator(new Accuracy())
        .optDevices(Engine.getInstance().getDevices(1))
        .addTrainingListeners(TrainingListener.Defaults.logging(outputDir))
        .addTrainingListeners(listener);
  }
}
