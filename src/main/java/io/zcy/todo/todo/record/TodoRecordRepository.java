package io.zcy.todo.todo.record;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TodoRecordRepository extends ReactiveCrudRepository<TodoRecord, Integer> {
  Flux<TodoRecord> findByUserId(Integer userId);

  Flux<TodoRecord> findByTodoId(Integer todoId);
}
