package io.zcy.todo.todo;

import static io.zcy.todo.Util.getTokenFrom;

import com.auth0.jwt.JWT;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TodoRecordHandler {
  @Resource private TodoRecordService service;

  public Mono<ServerResponse> createTodoRecord(ServerRequest request) {
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.notFound().build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    Integer todoId = Integer.valueOf(request.pathVariable("id"));
    return request
        .bodyToMono(TodoRecordDTO.class)
        .flatMap(todoRecordDTO -> service.createTodoRecord(todoRecordDTO, userId, todoId))
        .map(TodoRecordDTO::new)
        .flatMap(
            todoRecordDTO ->
                ServerResponse.status(HttpStatus.CREATED).body(todoRecordDTO, TodoRecordDTO.class));
  }
}
