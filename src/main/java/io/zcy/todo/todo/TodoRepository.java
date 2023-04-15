package io.zcy.todo.todo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TodoRepository extends ReactiveCrudRepository<Todo, Integer> {
    Flux<Todo> findByUserId(Integer userId);
}
