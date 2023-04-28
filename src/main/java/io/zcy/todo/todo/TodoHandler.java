package io.zcy.todo.todo;

import static io.zcy.todo.Util.getTokenFrom;

import com.auth0.jwt.JWT;
import jakarta.annotation.Resource;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TodoHandler {
  @Resource private TodoService service;

  public Mono<ServerResponse> getTodoById(ServerRequest request) {
    log.info("{}", request);
    Integer id = Integer.valueOf(request.pathVariable("id"));
    log.info("任务【{}】被读取", id);
    Mono<TodoDTO> todoDTO = service.getTodoById(id).map(TodoDTO::new);
    return ServerResponse.ok().body(todoDTO, TodoDTO.class);
  }

  public Mono<ServerResponse> getTodos(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    log.info("用户【{}】正在获取任务列表", userId);
    Flux<TodoDTO> todoDTO = service.getTodosByUser(userId).map(TodoDTO::new);
    return ServerResponse.ok().body(todoDTO, TodoDTO.class);
  }

  public Mono<ServerResponse> createTodo(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    log.info("用户【{}】正在创建任务", userId);
    Mono<TodoDTO> todoDTO =
        request
            .bodyToMono(TodoDTO.class)
            .map(
                todoDTO1 -> {
                  todoDTO1.setUserId(userId);
                  return todoDTO1;
                })
            .flatMap(service::createTodo)
            .map(TodoDTO::new);
    return ServerResponse.status(HttpStatus.CREATED).body(todoDTO, TodoDTO.class);
  }

  public Mono<ServerResponse> updateTodo(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Mono<TodoDTO> todoDTO =
        request.bodyToMono(TodoDTO.class).flatMap(service::updateTodo).map(TodoDTO::new);
    return ServerResponse.ok().body(todoDTO, TodoDTO.class);
  }

  public Mono<ServerResponse> deleteTodo(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer id = Integer.valueOf(request.pathVariable("id"));
    log.info("任务【{}】正在被删除", id);
    return service.deleteTodo(id).then(ServerResponse.noContent().build());
  }
}
