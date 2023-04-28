package io.zcy.todo.dl4j;

import io.zcy.todo.todo.record.TodoRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Lstm {
  @Resource private TodoRecordService todoRecordService;

  public void lstm() {}
}
