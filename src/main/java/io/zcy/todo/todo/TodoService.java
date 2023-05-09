package io.zcy.todo.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zcy.todo.Util;
import io.zcy.todo.account.Account;
import io.zcy.todo.account.AccountService;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TodoService {
  @Resource private TodoRepository repository;
  @Resource private AccountService accountService;

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
            todoDTO.getBeginDate(),
            todoDTO.getPlannedEndDate(),
            todoDTO.getTotalAmount(),
            todoDTO.getDescription());
    return repository.save(todo);
  }

  public Mono<Todo> updateTodo(TodoDTO todoDTO) {
    return repository
        .findById(todoDTO.getId())
        .map(
            todo -> {
              todo.setBeginDate(todoDTO.getBeginDate());
              todo.setPlannedEndDate(todoDTO.getPlannedEndDate());
              todo.setCurrentAmount(todoDTO.getCurrentAmount());
              todo.setTotalAmount(todoDTO.getTotalAmount());
              todo.setDescription(todoDTO.getDescription());
              if (todo.getCurrentAmount() >= todo.getTotalAmount()) {
                todo.setActualEndDate(LocalDate.now());
              }
              todo.setUpdateTime(LocalDateTime.now());
              return todo;
            })
        .flatMap(repository::save);
  }

  public Mono<Void> deleteTodo(Integer id) {
    return repository.deleteById(id);
  }

  public Flux<Todo> getProgram(Integer userId) {
    return accountService
        .getUserById(userId)
        .map(Account::getTimePerWeek)
        .map(
            time -> {
              ObjectMapper mapper = new ObjectMapper();
              try {
                List<Util.TimeObject> timeObject = mapper.readValue(time, new TypeReference<>() {});
                return timeObject.stream().map(Util.TimeObject::day).toArray(String[]::new);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            })
        .flatMapMany(Flux::fromArray)
        .flatMap(
            day -> {
              int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
              if (Integer.parseInt(day) == dayOfWeek % 7) {
                return getTodosByUser(userId)
                    .filter(todo -> todo.getActualEndDate() == null)
                    .sort(Comparator.comparing(Todo::getPlannedEndDate));
              }
              return Flux.empty();
            });
  }
}
