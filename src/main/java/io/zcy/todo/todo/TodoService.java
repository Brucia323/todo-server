package io.zcy.todo.todo;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TodoService {
  @Resource private TodoRepository repository;

  public Mono<Todo> getTodoById(Integer id) {
    return repository.findById(id);
  }

  public Flux<Todo> getTodosByUser(Integer userId) {
    return repository.findByUserId(userId);
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
    return repository.save(todo);
  }

  public Mono<Todo> updateTodo(TodoDTO todoDTO) {
    return repository
        .findById(todoDTO.getId())
        .map(
            todo -> {
              todo.setBeginTime(todoDTO.getBeginTime());
              todo.setPlannedEndTime(todoDTO.getPlannedEndTime());
              todo.setActualEndTime(todoDTO.getActualEndTime());
              todo.setCurrentAmount(todoDTO.getCurrentAmount());
              todo.setTotalAmount(todoDTO.getTotalAmount());
              todo.setDescription(todoDTO.getDescription());
              return todo;
            })
        .flatMap(repository::save);
  }

  public Mono<Void> deleteTodo(Integer id) {
    return repository.deleteById(id);
  }
}
