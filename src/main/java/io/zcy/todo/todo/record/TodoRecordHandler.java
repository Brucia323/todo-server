package io.zcy.todo.todo.record;

import static io.zcy.todo.Util.getTokenFrom;

import com.auth0.jwt.JWT;
import jakarta.annotation.Resource;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TodoRecordHandler {
  @Resource private TodoRecordService service;

  public Mono<ServerResponse> createTodoRecord(ServerRequest request) {
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    Integer todoId = Integer.valueOf(request.pathVariable("id"));
    log.info("用户【{}】正在更新任务【{}】进度", userId, todoId);
    Mono<TodoRecordDTO> todoRecordDTO =
        request
            .bodyToMono(TodoRecordDTO.class)
            .flatMap(todoRecordDTO1 -> service.createTodoRecord(todoRecordDTO1, userId, todoId))
            .map(TodoRecordDTO::new);
    return ServerResponse.status(HttpStatus.CREATED).body(todoRecordDTO, TodoRecordDTO.class);
  }
}
