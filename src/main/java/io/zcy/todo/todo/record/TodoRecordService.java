package io.zcy.todo.todo.record;

import org.springframework.stereotype.Service;

import io.zcy.todo.todo.Todo;
import io.zcy.todo.todo.TodoDTO;
import io.zcy.todo.todo.TodoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class TodoRecordService {
  @Resource
  private TodoRecordRepository repository;

  @Resource
  private TodoService parentService;

  public Flux<TodoRecord> getTodoRecordsByTodoId(Integer todoId) {
    return repository.findByTodoId(todoId);
  }

  public Flux<TodoRecord> getTodoRecordsByUserId(Integer userId) {
    return repository.findByUserId(userId);
  }

  public Mono<TodoRecord> createTodoRecord(
      TodoRecordDTO todoRecordDTO, Integer userId, Integer todoId) {
    Mono<Todo> todo = parentService.getTodoById(todoId);
    Mono<TodoRecord> todoRecord = todo.map(todo1 -> todoRecordDTO.getCurrentAmount() - todo1.getCurrentAmount())
        .map(amount -> new TodoRecord(userId, todoId, amount));
    todo.publishOn(Schedulers.boundedElastic())
        .map(TodoDTO::new)
        .flatMap(
            todoDTO -> {
              todoDTO.setCurrentAmount(todoRecordDTO.getCurrentAmount());
              return parentService.updateTodo(todoDTO);
            })
        .subscribe();
    return todoRecord.flatMap(repository::save);
  }
}
