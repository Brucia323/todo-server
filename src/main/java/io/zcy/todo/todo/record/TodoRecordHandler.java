package io.zcy.todo.todo.record;

import static io.zcy.todo.Util.getTokenFrom;

import java.util.Comparator;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TodoRecordHandler {
  @Resource
  private TodoRecordService service;

  public Mono<ServerResponse> createTodoRecord(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    Integer todoId = Integer.valueOf(request.pathVariable("id"));
    Mono<TodoRecordDTO> todoRecordDTO = request
        .bodyToMono(TodoRecordDTO.class)
        .flatMap(todoRecordDTO1 -> service.createTodoRecord(todoRecordDTO1, userId, todoId))
        .map(TodoRecordDTO::new);
    return ServerResponse.status(HttpStatus.CREATED).body(todoRecordDTO, TodoRecordDTO.class);
  }

  public Mono<ServerResponse> generateEfficiency(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    ObjectMapper mapper = new ObjectMapper();
    Flux<ObjectNode> objectNodeFlux = service
        .getTodoRecordsByUserId(userId)
        .groupBy(todoRecord -> todoRecord.getCreateTime().toLocalDate())
        .flatMap(
            group -> group.reduce(
                (r1, r2) -> new TodoRecord(r1.getAmount() + r2.getAmount(), r1.getCreateTime())))
        .sort(Comparator.comparing(a -> a.getCreateTime().toLocalDate()))
        .map(
            todoRecord -> {
              ObjectNode node = mapper.createObjectNode();
              node.put("amount", todoRecord.getAmount());
              node.put("time", todoRecord.getCreateTime().toLocalDate().toString());
              return node;
            });
    return ServerResponse.ok().body(objectNodeFlux, ObjectNode.class);
  }
}
