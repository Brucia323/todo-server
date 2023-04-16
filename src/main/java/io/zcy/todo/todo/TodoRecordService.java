package io.zcy.todo.todo;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TodoRecordService {
  @Resource private TodoRecordRepository repository;

  public Flux<TodoRecord> getTodoRecordsByTodoId(Integer todoId) {
    return repository.findByTodoId(todoId);
  }

  public Flux<TodoRecord> getTodoRecordsByUserId(Integer userId) {
    return repository.findByUserId(userId);
  }
}
