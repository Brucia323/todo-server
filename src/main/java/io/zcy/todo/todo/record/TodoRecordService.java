package io.zcy.todo.todo.record;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TodoRecordService {
  @Resource private TodoRecordRepository repository;

  public Flux<TodoRecord> getTodoRecordsByTodoId(Integer todoId) {
    return repository.findByTodoId(todoId);
  }

  public Flux<TodoRecord> getTodoRecordsByUserId(Integer userId) {
    return repository.findByUserId(userId);
  }

  public Mono<TodoRecord> createTodoRecord(
      TodoRecordDTO todoRecordDTO, Integer userId, Integer todoId) {
    TodoRecord todoRecord = new TodoRecord(userId, todoId, todoRecordDTO.getCurrentAmount());
    return repository.save(todoRecord);
  }
}
