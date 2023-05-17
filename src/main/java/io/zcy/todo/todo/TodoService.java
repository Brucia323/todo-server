package io.zcy.todo.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zcy.todo.Util;
import io.zcy.todo.account.Account;
import io.zcy.todo.account.AccountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class TodoService {
  @Resource
  private TodoRepository repository;
  @Resource
  private AccountService accountService;

  public Mono<Todo> getTodoById(Integer id) {
    return repository.findById(id);
  }

  public Flux<Todo> getTodosByUser(Integer userId) {
    return repository.findByUserIdOrderByIdDesc(userId);
  }

  public Mono<Todo> createTodo(TodoDTO todoDTO) {
    Todo todo = new Todo(
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
          todo.setName(todoDTO.getName());
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

  /**
   * 此函数根据特定用户的每周计划返回未完成待办事项的 Flux。
   *
   * @param userId 为其检索程序的用户的 ID。
   * @return 正在返回 `Todo` 对象的 `Flux`。
   */
  public Flux<Todo> getProgram(Integer userId) {
    // 此代码在 `TodoService` 类中定义了一个函数 `getProgram`，它根据特定用户的每周计划返回 `Todo` 对象的 `Flux`。
    return accountService
      .getUserById(userId)
      .map(Account::getTimePerWeek)
      .map(
        time -> {
          // `ObjectMapper mapper = new ObjectMapper();` 创建 `ObjectMapper` 类的新实例，它是
          // Jackson JSON
          // 处理库的一部分。 `ObjectMapper` 类提供读取和写入 JSON 数据的方法，在此代码中用于将 JSON 字符串反序列化为
          // `Util.TimeObject`
          // 对象列表。
          ObjectMapper mapper = new ObjectMapper();
          // 此代码块使用 Jackson JSON 处理库将 JSON 字符串反序列化为“Util.TimeObject”对象列表。然后它将每个 TimeObject
          // 映射到它的
          // day 属性，并返回一个 String 对象数组，表示在 TimeObject 列表中指定的星期几。如果在反序列化期间发生错误，则会抛出
          // RuntimeException。
          try {
            // 这行代码使用 Jackson JSON 处理库将 JSON 字符串“time”反序列化为“Util.TimeObject”对象列表。
            // `ObjectMapper`
            // 类的`readValue` 方法用于此目的，`TypeReference`
            // 类用于指定反序列化对象的类型。空菱形运算符“<>”用于从上下文中推断反序列化对象的类型。
            List<Util.TimeObject> timeObject = mapper.readValue(time, new TypeReference<>() {
            });
            // 这行代码获取名为 timeObject 的 Util.TimeObject 对象列表，并使用 Stream 接口的 map 方法将每个对象映射到其 day
            // 属性。
            // `Util.TimeObject::day` 语法是一个方法引用，引用了`Util.TimeObject` 类的`day` getter 方法。然后使用
            // `toArray` 方法将生成的 `Stream<String>` 转换为 `String[]` 数组，并将对 `String`
            // 构造函数的方法引用作为参数。这将创建一个 String 对象数组，表示在 timeObject 列表中指定的星期几。
            return timeObject.stream().map(Util.TimeObject::day).toArray(String[]::new);
          } catch (JsonProcessingException e) {
            // `throw new RuntimeException(e);` 正在抛出一个新的 `RuntimeException`，其原因是
            // `JsonProcessingException` `e`。这样做是为了处理将 JSON
            // 字符串反序列化为“Util.TimeObject”对象列表期间可能发生的任何错误。
            throw new RuntimeException(e);
          }
        })
      .flatMapMany(Flux::fromArray)
      .flatMap(
        day -> {
          // `int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();` 正在使用 `LocalDate
          // .now()`
          // 方法，然后将其存储在 `dayOfWeek` 变量中。
          int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
          // 此代码块检查当前星期几是否与用户每周计划中指定的日期相匹配。如果是，它会为用户检索所有尚未完成、开始日期早于今天的待办事项，并按计划结束日期对它们进行排序。然后它返回这些过滤和排序的
          // todo 的 Flux。如果一周中的当前日期与用户每周计划中指定的日期不匹配，则返回一个空的 Flux。
          if (Integer.parseInt(day) == dayOfWeek % 7) {
            // 此代码正在过滤和排序特定用户的待办事项列表。它首先使用 `getTodosByUser`
            // 方法检索用户的所有待办事项，然后过滤掉所有已经完成的待办事项（`todo.getActualEndDate() ==
            // null`）和尚未开始的所有待办事项（`todo.getBeginDate`
            // ().isBefore(LocalDate.now())`).最后，它根据计划的结束日期（`Comparator.comparing(Todo::getPlannedEndDate)`）对剩余的待办事项进行排序。生成的过滤和排序的待办事项列表作为
            // Flux 返回。
            return getTodosByUser(userId)
              .filter(todo -> todo.getActualEndDate() == null)
              .filter(todo -> todo.getPlannedEndDate() != null)
              .filter(todo -> todo.getBeginDate() == null || !todo.getBeginDate().isAfter(LocalDate.now()))
              .sort(Comparator.comparing(Todo::getPlannedEndDate));
          }
          // `return Flux.empty();` 返回一个空的 `Flux`。在 `getProgram`
          // 方法的上下文中，如果当前星期几与用户每周计划中指定的任何一天都不匹配，则它用于返回一个空的
          // `Flux`。这是因为如果当前日期与计划中的任何日期都不匹配，则不会返回任何待办事项。
          return Flux.empty();
        });
  }
}
