package io.zcy.todo.todo;

import com.auth0.jwt.JWT;
import io.zcy.todo.Util;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TodoHandler {
  @Resource private TodoService service;

  public Mono<ServerResponse> getTodoById(ServerRequest request) {
    return Mono.just(Integer.valueOf(request.pathVariable("id")))
        .flatMap(service::getTodoById)
        .map(TodoDTO::new)
        .flatMap(todoDTO -> ServerResponse.ok().body(todoDTO, TodoDTO.class));
  }

  public Flux<ServerResponse> getTodos(ServerRequest request) {
    Optional<String> token = Util.getTokenFrom(request);
    if (token.isEmpty()) {
      return Flux.from(ServerResponse.notFound().build());
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    return service
        .getTodosByUser(userId)
        .map(TodoDTO::new)
        .flatMap(todoDTO -> ServerResponse.ok().body(todoDTO, TodoDTO.class));
  }

  public Mono<ServerResponse> createTodo(ServerRequest request) {
    Optional<String> token = Util.getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.notFound().build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    return request
        .bodyToMono(TodoDTO.class)
        .map(
            todoDTO -> {
              todoDTO.setUserId(userId);
              return todoDTO;
            })
        .flatMap(service::createTodo)
        .map(TodoDTO::new)
        .flatMap(todoDTO -> ServerResponse.status(HttpStatus.CREATED).body(todoDTO, TodoDTO.class));
  }

  public Mono<ServerResponse> updateTodo(ServerRequest request) {
    Optional<String> token = Util.getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.notFound().build();
    }
    return request
        .bodyToMono(TodoDTO.class)
        .flatMap(service::updateTodo)
        .map(TodoDTO::new)
        .flatMap(todoDTO -> ServerResponse.ok().body(todoDTO, TodoDTO.class));
  }

  public Mono<ServerResponse> deleteTodo(ServerRequest request) {
    Optional<String> token = Util.getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.notFound().build();
    }
    return Mono.just(Integer.valueOf(request.pathVariable("id")))
        .flatMap(service::deleteTodo)
        .then(ServerResponse.noContent().build());
  }
}
