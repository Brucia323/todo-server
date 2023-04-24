package io.zcy.todo.todo;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TodoService {
  @Resource private TodoRepository repository;

  public Mono<Todo> getTodoById(Integer id) {
    return repository.findById(id);
  }

  public Flux<Todo> getTodosByUser(Integer userId) {
    return repository.findByUserIdOrderByIdDesc(userId);
  }

  public Mono<Todo> createTodo(TodoDTO todoDTO) {
    Todo todo =
        new Todo(
            todoDTO.getUserId(),
            todoDTO.getName(),
            todoDTO.getBeginTime(),
            todoDTO.getPlannedEndTime(),
            todoDTO.getTotalAmount(),
            todoDTO.getDescription());
    log.info("任务正在创建: {}", todo);
    return repository.save(todo);
  }

  public Mono<Todo> updateTodo(TodoDTO todoDTO) {
    return repository
        .findById(todoDTO.getId())
        .map(
            todo -> {
              todo.setBeginTime(todoDTO.getBeginTime());
              todo.setPlannedEndTime(todoDTO.getPlannedEndTime());
              todo.setCurrentAmount(todoDTO.getCurrentAmount());
              todo.setTotalAmount(todoDTO.getTotalAmount());
              todo.setDescription(todoDTO.getDescription());
              if (todo.getCurrentAmount() >= todo.getTotalAmount()) {
                todo.setActualEndTime(LocalDate.now());
              }
              todo.setUpdateTime(LocalDateTime.now());
              log.info("任务正在被更新: {}", todo);
              return todo;
            })
        .flatMap(repository::save);
  }

  public Mono<Void> deleteTodo(Integer id) {
    return repository.deleteById(id);
  }
}
