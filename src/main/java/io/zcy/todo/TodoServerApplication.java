package io.zcy.todo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tensorflow.TensorFlow;

@SpringBootApplication
@Slf4j
public class TodoServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(TodoServerApplication.class, args);
    log.info("TensorFlow version: {}", TensorFlow.version());
  }
}
