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

  /**
   * 此函数创建一个新的 TodoRecord 对象，并在将 TodoRecord 对象保存到存储库之前用新的当前数量更新相应的 Todo 对象。
   * 
   * @param todoRecordDTO 一个对象，包含有关要创建的待办事项记录的信息，例如当前完成的任务数量
   * @param userId        创建待办事项记录的用户的 ID。
   * @param todoId        TodoRecord 关联的 Todo 对象的 ID。
   * @return 该方法返回一个 TodoRecord 类型的 Mono 对象。
   */
  public Mono<TodoRecord> createTodoRecord(
      TodoRecordDTO todoRecordDTO, Integer userId, Integer todoId) {
    // `Mono<Todo> todo = parentService.getTodoById(todoId);` 正在创建一个 `Mono` 对象，它将通过其
    // ID (`todoId`) 从
    // `parentService` 异步检索 `Todo` 对象。 `Mono` 对象表示 0 或 1 个 `Todo`
    // 对象的流，检索操作将在单独的线程上执行。
    Mono<Todo> todo = parentService.getTodoById(todoId);
    // 此代码通过将从 parentService 异步检索的 `Todo` 对象映射到新的 `TodoRecord` 对象来创建类型为 `TodoRecord`
    // 的新 `Mono` 对象。
    // `map` 函数采用一个 lambda 表达式，该表达式从 `todoRecordDTO` 对象中的 `currentAmount` 中减去 `Todo`
    // 对象的
    // `currentAmount`
    // 以获得当前数量的差值。然后使用此差异创建一个新的“TodoRecord”对象，并将“userId”、“todoId”和“amount”作为参数。
    Mono<TodoRecord> todoRecord = todo.map(todo1 -> todoRecordDTO.getCurrentAmount() - todo1.getCurrentAmount())
        .map(amount -> new TodoRecord(userId, todoId, amount));
    // 这段代码订阅了一个 `Mono` 对象 `todo` 并对其执行了一系列操作。
    todo.publishOn(Schedulers.boundedElastic())
        .map(TodoDTO::new)
        .flatMap(
            todoDTO -> {
              // `todoDTO.setCurrentAmount(todoRecordDTO.getCurrentAmount());` 将`TodoDTO`
              // 对象的当前数量设置为`todoRecordDTO` 对象中指定的当前数量。这是在调用 parentService 对象的 updateTodo
              // 方法之前完成的，该方法使用新的当前数量更新 Todo 对象。
              todoDTO.setCurrentAmount(todoRecordDTO.getCurrentAmount());
              // `return parentService.updateTodo(todoDTO);` 正在调用 `parentService` 对象的
              // `updateTodo`
              // 方法，并传入一个 `TodoDTO`
              // 对象作为参数。此方法使用新的当前金额更新相应的“Todo”对象，并返回表示更新后的“Todo”对象的“Mono”对象。
              // `flatMap` 运算符用于将此操作与之前的 `map` 运算符链接起来，以便在将 `TodoRecord` 对象保存到存储库之前更新 `Todo`
              // 对象。
              return parentService.updateTodo(todoDTO);
            })
        .subscribe();
    // `return todoRecord.flatMap(repository::save);` 正在返回一个 `Mono` 对象，表示将
    // `TodoRecord` 对象保存到存储库的结果。
    // `flatMap` 运算符用于将 `todoRecord` 返回的 `Mono` 对象与 `repository` 对象的 `save`
    // 方法链接起来。这意味着“TodoRecord”对象将保存到存储库中，生成的“Mono”对象将发出保存的“TodoRecord”对象。如果在保存操作期间出现错误，`Mono`
    // 对象将改为发出错误信号。
    return todoRecord.flatMap(repository::save);
  }
}
