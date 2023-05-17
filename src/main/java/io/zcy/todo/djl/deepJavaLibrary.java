package io.zcy.todo.djl;

//import ai.djl.Model;
//import ai.djl.engine.Engine;
//import ai.djl.metric.Metrics;
//import ai.djl.ndarray.NDArray;
//import ai.djl.ndarray.NDManager;
//import ai.djl.ndarray.types.Shape;
//import ai.djl.nn.Block;
//import ai.djl.nn.Blocks;
//import ai.djl.nn.SequentialBlock;
//import ai.djl.nn.core.Linear;
//import ai.djl.nn.norm.BatchNorm;
//import ai.djl.nn.recurrent.LSTM;
//import ai.djl.training.DefaultTrainingConfig;
//import ai.djl.training.EasyTrain;
//import ai.djl.training.Trainer;
//import ai.djl.training.TrainingResult;
//import ai.djl.training.dataset.ArrayDataset;
//import ai.djl.training.evaluator.Accuracy;
//import ai.djl.training.listener.SaveModelTrainingListener;
//import ai.djl.training.listener.TrainingListener;
//import ai.djl.training.loss.Loss;
//import ai.djl.training.util.ProgressBar;
//import ai.djl.translate.TranslateException;
//import io.zcy.todo.todo.record.TodoRecord;
//import io.zcy.todo.todo.record.TodoRecordService;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Comparator;
//import java.util.List;

//@Component
//@Slf4j
public class DeepJavaLibrary {
//  @Resource
//  private TodoRecordService todoRecordService;
//
//  /**
//   * 此 Java 函数使用 LSTM 在按日期分组的 TodoRecord 对象列表上训练模型并返回训练结果。
//   *
//   * @param userId 为其训练 LSTM 模型的用户的 ID。
//   * @return 该方法返回一个 TrainingResult 对象。
//   */
//  public TrainingResult lstm(Integer userId) throws IOException, TranslateException {
//    // 此代码基于给定的 userId 从服务中检索 TodoRecord 对象的列表，按它们的 createTime 日期对它们进行分组，将每个组减少为单个
//    // TodoRecord 对象及其
//    // amount 值的总和，按 createTime 日期对结果列表进行排序，并将它们收集到一个 List 中。 `block()`
//    // 方法用于阻塞并等待结果可用。
//    List<TodoRecord> todoRecords = todoRecordService
//        .getTodoRecordsByUserId(userId)
//        .groupBy(todoRecord -> todoRecord.getCreateTime().toLocalDate())
//        .flatMap(
//            group -> group.reduce(
//                (r1, r2) -> new TodoRecord(r1.getAmount() + r2.getAmount(), r1.getCreateTime())))
//        .sort(Comparator.comparing(a -> a.getCreateTime().toLocalDate()))
//        .collectList()
//        .block();
//    // 此代码检查 `todoRecords` 列表是否为空，如果为空，则抛出带有消息“暂无数据”（中文意思是“无数据可用”）的
//    // RuntimeException。
//    if (todoRecords == null) {
//      // `throw new RuntimeException("暂无数据");` 如果 `todoRecords` 列表为空，则抛出
//      // `RuntimeException`
//      // 并显示消息“暂无数据”（中文意思是“无可用数据”），表明有没有可用于训练 LSTM 模型的数据。
//      throw new RuntimeException("暂无数据");
//    }
//    // 这行代码通过将每个 TodoRecord 对象映射到它的 createTime 日期，然后使用 toList() 方法将生成的 LocalDate
//    // 对象收集到一个新列表中，从
//    // todoRecords 列表中创建一个 LocalDate 对象列表。
//    List<LocalDate> dates = todoRecords.stream().map(todoRecord -> todoRecord.getCreateTime().toLocalDate()).toList();
//    // 这行代码通过将 `todoRecords` 列表中每个 `TodoRecord` 对象的 `amount` 字段映射到一个 Integer 对象，然后使用
//    // `mapToInt(
//    // Integer::valueOf)`，最后使用 `toArray()`
//    // 将生成的整数流收集到一个数组中。这将创建列表中每个“TodoRecord”对象的“数量”值数组，该数组将用作训练
//    // LSTM 模型的输入数据。
//    int[] amounts = todoRecords.stream().map(TodoRecord::getAmount).mapToInt(Integer::valueOf).toArray();
//    // 此代码块在按日期分组的 TodoRecord 对象列表上训练 LSTM 模型。它首先创建一个 NDManager 实例，为输入数据和标签创建
//    // NDArrays，从数据和标签创建一个
//    // ArrayDataset，并准备数据集。然后它创建一个新的 LSTM 模型，将其块设置为在 getLSTMModel() 方法中创建的 LSTM
//    // 模型，使用
//    // setupTrainingConfig() 方法设置训练配置，并使用模型和配置创建一个新的 Trainer
//    // 实例。它使用输入数据的形状初始化训练器，将指标设置为新的 Metrics
//    // 实例，并使用 EasyTrain.fit() 方法训练一个时期的模型。最后，它从训练器返回 TrainingResult 对象。
//    try (NDManager manager = NDManager.newBaseManager()) {
//      // `NDArray labels = manager.arange(amounts.length);` 正在创建从 0 到 `amounts`
//      // 数组长度的整数
//      // NDArray。该数组将用作训练期间输入数据的标签。
//      NDArray labels = manager.arange(amounts.length);
//      // `NDArray data = manager.create(amounts);` 正在从 `amounts` 数组创建一个 NDArray。此
//      // NDArray 将用作训练 LSTM
//      // 模型的输入数据。
//      NDArray data = manager.create(amounts);
//      // 这行代码根据输入数据和标签创建一个“ArrayDataset”对象。 `optLabels()` 方法设置数据集的标签，`setData()`
//      // 方法设置输入数据，`setSampling()` 方法设置采样率以及是否打乱数据。最后，`build()` 方法创建 ArrayDataset 对象。
//      ArrayDataset arrayDataset = new ArrayDataset.Builder().optLabels(labels).setData(data).setSampling(1, false)
//          .build();
//      // `arrayDataset.prepare(new ProgressBar());` 正在通过预取和预处理数据来准备用于训练的
//      // `ArrayDataset`。 `ProgressBar`
//      // 用于显示准备过程的进度。
//      arrayDataset.prepare(new ProgressBar());
//      try (Model model = Model.newInstance("lstm")) {
//        // `model.setBlock(getLSTMModel());` 是将神经网络模型的块设置为由 `getLSTMModel()` 方法创建的 LSTM
//        // 模型。这意味着神经网络模型将具有由 LSTM 模型定义的架构，包括 LSTM 层、批量归一化层、批量展平层和线性层。
//        model.setBlock(getLSTMModel());
//        DefaultTrainingConfig config = setupTrainingConfig();
//        try (Trainer trainer = model.newTrainer(config)) {
//          trainer.setMetrics(new Metrics());
//          Shape shape = new Shape(1, arrayDataset.size(), 1);
//          trainer.initialize(shape);
//          EasyTrain.fit(trainer, 1, arrayDataset, arrayDataset);
//          return trainer.getTrainingResult();
//        }
//      }
//    }
//  }
//
//  /**
//   * 此函数返回具有指定状态大小、层数和线性输出层的 LSTM 模型。
//   *
//   * @return 表示状态大小为 64、一层、无遗忘和 10 个输出单元的长短期记忆 (LSTM) 模型的 Block
//   *         对象。该模型还包括批量归一化和线性层。
//   */
//  private static Block getLSTMModel() {
//    // `SequentialBlock block = new SequentialBlock();` 创建一个新的顺序块实例，这是 Deep Java 库
//    // (DJL)
//    // 中的一种神经网络块。顺序块允许创建按顺序执行的一系列神经网络层。在此特定代码中，LSTM 层、批量归一化层、批量扁平化层和线性层按顺序添加到顺序块中。
//    SequentialBlock block = new SequentialBlock();
//    // 此代码将 LSTM 层添加到神经网络模型。 LSTM 层是使用具有以下参数的 LSTM.Builder() 方法创建的：
//    // - setStateSize(64)：将 LSTM 状态的大小设置为 64
//    // - setNumLayers(1)：将 LSTM 层数设置为 1
//    // - optDropRate(0)：将遗忘率设置为 0（无遗忘）
//    // - optReturnState(false)：设置是否返回LSTM层的最终状态（本例为false）
//    // 最后，使用 block.add() 方法将 LSTM 层添加到 SequentialBlock 对象。
//    block.add(
//        new LSTM.Builder()
//            .setStateSize(64)
//            .setNumLayers(1)
//            .optDropRate(0)
//            .optReturnState(false)
//            .build());
//    // `block.add(BatchNorm.builder().optEpsilon(1e-5f).optMomentum(0.9f).build());`
//    // 正在向神经网络模型添加批量归一化层。批量归一化是一种通过归一化每一层的输入来改进深度神经网络训练的技术。 `optEpsilon`
//    // 参数设置添加到方差的小常量以避免被零除，`optMomentum` 参数设置均值和方差的移动平均值的动量。
//    block.add(BatchNorm.builder().optEpsilon(1e-5f).optMomentum(0.9f).build());
//    // `block.add(Blocks.batchFlattenBlock());` 正在向神经网络模型中添加一个批量展平块。 Batch flatten
//    // 是一种用于将卷积层或循环层的输出转换为可以馈送到全连接层的 2D 矩阵的技术。这是必要的，因为全连接层需要 2D 输入，而卷积层和循环层产生 3D 或
//    // 4D 输出。 batch flatten
//    // 块将前一层的输出扁平化为二维矩阵，然后可以将其馈送到线性层。
//    block.add(Blocks.batchFlattenBlock());
//    // `block.add(Linear.builder().setUnits(10).build());` 正在向具有 10
//    // 个输出单元的神经网络模型添加一个线性层。线性层是一个完全连接的层，它从前一层获取扁平化的输出并对其应用线性变换，产生大小为 10
//    // 的输出。该层通常用作神经网络中用于分类任务的最后一层，其中输出对应于预测的类别概率。
//    block.add(Linear.builder().setUnits(10).build());
//    // `return block;` 返回在 `getLSTMModel()` 方法中创建的 LSTM 模型块。此块表示具有特定架构的神经网络模型，包括
//    // LSTM
//    // 层、批量归一化层、批量展平层和线性层。此块可用于在给定数据集上训练和评估模型。
//    return block;
//  }
//
//  /**
//   * 此函数为机器学习模型设置训练配置，并包含一个用于在训练期间保存模型的侦听器。
//   *
//   * @return 该方法返回一个 DefaultTrainingConfig 对象。
//   */
//  private static DefaultTrainingConfig setupTrainingConfig() {
//    // `String outputDir = "/build/model";` 正在设置将保存训练模型的输出目录。
//    String outputDir = "/build/model";
//    // `SaveModelTrainingListener` 是 DJL
//    // 库中的训练监听器，它在每个训练周期后保存模型。在此代码中，它使用指定为“outputDir”的输出目录进行实例化。然后使用
//    // addTrainingListeners()
//    // 方法将侦听器添加到训练配置中。这确保了模型在训练期间被定期保存，允许它被重新加载并在以后用于推理或进一步训练。
//    SaveModelTrainingListener listener = new SaveModelTrainingListener(outputDir);
//    // `listener.setSaveModelCallback()`
//    // 正在设置一个回调函数，每次在训练期间保存模型时都会调用该回调函数。回调函数检索当前的训练结果和模型，计算准确率和损失，并将这些值设置为模型的属性。这允许稍后在模型用于推理或进一步训练时轻松检索准确性和损失。
//    listener.setSaveModelCallback(
//        trainer -> {
//          // `TrainingResult result = trainer.getTrainingResult();` 从 trainer
//          // 对象中检索训练结果，其中包括训练和验证损失、准确性和其他指标等信息。此信息可用于评估训练模型的性能，并做出有关进一步训练或模型选择的决策。
//          TrainingResult result = trainer.getTrainingResult();
//          // `Model model = trainer.getModel();` 正在从 `Trainer`
//          // 对象中检索经过训练的模型。这允许模型用于推理或进一步训练。
//          Model model = trainer.getModel();
//          // `float accuracy = result.getValidateEvaluation("Accuracy");` 正在从
//          // `TrainingResult`
//          // 对象中检索准确度指标。在训练期间，模型在验证数据集上进行评估，计算准确度指标并将其存储在“TrainingResult”对象中。这行代码检索该准确度指标并将其存储在名为“accuracy”的浮点变量中。
//          float accuracy = result.getValidateEvaluation("Accuracy");
//          // `model.setProperty("Accuracy", String.format("%.5f", accuracy));`
//          // 在训练模型上设置一个名为“Accuracy”的属性，其值为训练期间计算的准确度指标值。 `String.format("%.5f", accuracy)`
//          // 部分将精度值格式化为带
//          // 5 个小数位的字符串。稍后可以从模型中检索此属性，并用于评估或与其他模型进行比较。
//          model.setProperty("Accuracy", String.format("%.5f", accuracy));
//          // `model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
//          // 正在训练模型上设置名为“Loss”的属性。该属性的值为模型在训练过程中的验证损失，格式为保留 5
//          // 位小数的字符串。稍后可以从模型中检索此属性并用于评估或与其他模型进行比较。
//          model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
//        });
//
//    // 此代码正在为机器学习模型设置训练配置。它创建了一个新的“DefaultTrainingConfig”对象，其损失函数为“maskedSoftmaxCrossEntropyLoss()”，添加了一个准确度评估器，设置了用于训练的设备，并添加了用于记录和保存模型的训练侦听器。
//    // `listener` 对象是一个
//    // `SaveModelTrainingListener`，它在每个训练时期后保存模型并设置模型的准确性和损失属性。该方法返回整个配置。
//    return new DefaultTrainingConfig(Loss.maskedSoftmaxCrossEntropyLoss())
//        .addEvaluator(new Accuracy())
//        .optDevices(Engine.getInstance().getDevices(1))
//        .addTrainingListeners(TrainingListener.Defaults.logging(outputDir))
//        .addTrainingListeners(listener);
//  }
}
