package io.zcy.todo.todo;

import static io.zcy.todo.Util.getTokenFrom;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.auth0.jwt.JWT;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TodoHandler {
  @Resource
  private TodoService service;

  public Mono<ServerResponse> getTodoById(ServerRequest request) {
    log.info("{}", request);
    Integer id = Integer.valueOf(request.pathVariable("id"));
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
    Mono<TodoDTO> todoDTO = request
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
    Mono<TodoDTO> todoDTO = request.bodyToMono(TodoDTO.class).flatMap(service::updateTodo).map(TodoDTO::new);
    return ServerResponse.ok().body(todoDTO, TodoDTO.class);
  }

  public Mono<ServerResponse> deleteTodo(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer id = Integer.valueOf(request.pathVariable("id"));
    return service.deleteTodo(id).then(ServerResponse.noContent().build());
  }

  /**
   * 此函数基于令牌检索程序并返回 TodoDTO 对象列表或如果没有元素则返回空列表。
   * 
   * @param request 请求对象表示服务器接收到的传入 HTTP 请求。它包含 HTTP 方法、标头、查询参数和请求正文等信息。
   * @return 该方法返回一个 ServerResponse 的 Mono。
   */
  public Mono<ServerResponse> getProgram(ServerRequest request) {
    // `log.info("{}", request);` 使用 SLF4J 日志记录框架记录传入请求的信息。 `{}`
    // 是请求对象的占位符，在生成日志消息时将替换为实际对象。
    log.info("{}", request);
    // `Optional<String> token = getTokenFrom(request);` 正在从 HTTP 请求标头中检索 JWT
    // 令牌（如果存在）。它返回一个可能包含也可能不包含令牌的“可选”对象。 `getTokenFrom`
    // 方法可能是一种实用方法，它使用特定的键或格式从标头中提取令牌。
    Optional<String> token = getTokenFrom(request);
    // 此代码块检查 `Optional<String> token` 对象是否为空。如果为空，则表示 HTTP 请求标头中不存在 JWT
    // 令牌，并且该方法返回状态代码为“401
    // Unauthorized”的 HTTP 响应。这是一种安全措施，可确保只有授权用户才能访问应用程序的受保护资源。
    if (token.isEmpty()) {
      // `return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();` 正在返回状态代码为
      // 401（未授权）的 HTTP
      // 响应。这通常用于指示用户无权访问所请求的资源。
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    // 这行代码正在解码 JWT 令牌以检索“id”声明并将其转换为整数值。 “id”声明假定存在于 JWT 令牌中，并包含与令牌关联的用户 ID。此用户 ID
    // 用于检索用户的相关待办事项。
    Integer id = JWT.decode(token.get()).getClaim("id").asInt();
    // 通过使用构造函数引用“TodoDTO::new”将“TodoService”的“getProgram”方法的结果映射到 TodoDTO 对象，创建
    // TodoDTO 对象的 Flux。
    // `getProgram` 方法可能会返回某种类型的数据的 Flux，这些数据正在转换为 TodoDTO 对象。
    Flux<TodoDTO> todoDTO = service.getProgram(id).map(TodoDTO::new);
    // 此代码正在检查 `Flux<TodoDTO>` 对象 `todoDTO` 是否有任何元素。如果是，它会返回状态代码为 200 的 HTTP
    // 响应，并将“todoDTO”对象作为响应主体。如果它没有任何元素，它会返回状态代码为 200 的 HTTP 响应和一个空列表作为响应主体。
    // `hasElements()` 方法返回一个
    // `Mono<Boolean>`，如果 `Flux` 至少有一个元素则返回 `true`，否则返回 `false`。 `flatMap()` 方法用于将
    // `Mono<Boolean>` 转换为
    // `Mono<ServerResponse>`。
    return todoDTO
        .hasElements()
        .flatMap(
            hasElements -> hasElements
                ? ServerResponse.ok().body(todoDTO, TodoDTO.class)
                : ServerResponse.ok().bodyValue(new ArrayList<>().toString()));
  }
}
