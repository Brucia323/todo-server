package io.zcy.todo.djl;

import ai.djl.training.TrainingResult;
import ai.djl.translate.TranslateException;
import jakarta.annotation.Resource;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DeepJavaLibraryTest {
  @Resource private DeepJavaLibrary deepJavaLibrary;

  @Test
  void lstm() throws TranslateException, IOException {
    TrainingResult result = deepJavaLibrary.lstm(1);
    Assertions.assertNotNull(result);
  }
}
