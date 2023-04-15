package io.zcy.todo.todo;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TodoService {
  @Resource private TodoRepository repository;
  @Resource private TodoRecordService service;

  public Mono<TodoDTO> getTodoById(Integer todoId) {
    return repository
        .findById(todoId)
        .map(
            todo -> {
              Flux<TodoRecordDTO> todoRecordsDTO = service.getTodoRecordsByTodoId(todoId);
              return new TodoDTO(todo, todoRecordsDTO);
            });
  }

  public Flux<TodoDTO> getTodosByUser(Integer userId) {
    return repository.findByUserId(userId).map(TodoDTO::new);
  }

  public Mono<TodoDTO> createTodo(TodoDTO todoDTO) {
    Todo todo =
        new Todo(
            todoDTO.getUserId(),
            todoDTO.getBeginTime(),
            todoDTO.getPlannedEndTime(),
            todoDTO.getTotalAmount(),
            todoDTO.getDescription());
    return repository.save(todo).map(TodoDTO::new);
  }

  public Mono<TodoDTO> updateTodo(TodoDTO todoDTO) {
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
        .flatMap(repository::save)
        .map(TodoDTO::new);
  }

  public Mono<Void> deleteTodo(TodoDTO todoDTO) {
    return repository.deleteById(todoDTO.getId());
  }
}
