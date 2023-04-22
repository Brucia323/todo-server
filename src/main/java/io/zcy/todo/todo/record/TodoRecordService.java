package io.zcy.todo.todo.record;

import io.zcy.todo.todo.Todo;
import io.zcy.todo.todo.TodoDTO;
import io.zcy.todo.todo.TodoRepository;
import io.zcy.todo.todo.TodoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class TodoRecordService {
  @Resource private TodoRecordRepository repository;

  @Resource private TodoService parentService;

  public Flux<TodoRecord> getTodoRecordsByTodoId(Integer todoId) {
    return repository.findByTodoId(todoId);
  }

  public Flux<TodoRecord> getTodoRecordsByUserId(Integer userId) {
    return repository.findByUserId(userId);
  }

  public Mono<TodoRecord> createTodoRecord(
      TodoRecordDTO todoRecordDTO, Integer userId, Integer todoId) {
    TodoRecord todoRecord = new TodoRecord(userId, todoId, todoRecordDTO.getCurrentAmount());
    parentService
        .getTodoById(todoId)
        .publishOn(Schedulers.boundedElastic())
        .map(TodoDTO::new)
        .flatMap(
            todoDTO -> {
              todoDTO.setCurrentAmount(todoRecord.getCurrentAmount());
              return parentService.updateTodo(todoDTO);
            });
    return repository.save(todoRecord);
  }
}
