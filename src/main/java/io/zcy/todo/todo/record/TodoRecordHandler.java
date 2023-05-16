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

  /**
   * 此函数为按日期分组的用户待办事项记录生成效率数据，并将其作为 ServerResponse 中的 JSON 对象的 Flux 返回。
   * 
   * @param request 包含有关传入 HTTP 请求的信息的请求对象，例如标头、查询参数和请求正文。
   * @return 正在返回 ServerResponse 的 Mono。
   */
  public Mono<ServerResponse> generateEfficiency(ServerRequest request) {
    // `log.info("{}", request); 正在使用 `slf4j` 日志框架提供的 `log` 对象在 `INFO` 级别记录
    // `request` 对象的信息。 `{}` 是
    // `request` 对象的占位符，在生成日志消息时将替换为 `request` 对象的实际值。
    log.info("{}", request);
    // `Optional<String> token = getTokenFrom(request);` 正在调用实用程序方法 `getTokenFrom` 从
    // HTTP 请求标头中提取 JWT
    // 令牌。该方法返回一个可能包含也可能不包含令牌的“可选”对象。然后将 `Optional` 对象分配给 `token` 变量，该变量可用于使用
    // `isPresent()` 方法检查令牌是否存在。
    Optional<String> token = getTokenFrom(request);
    // 此代码块检查“可选”对象“令牌”是否为空。如果为空，则表示 HTTP 请求标头中不存在 JWT 令牌，该方法会在 `ServerResponse` 中返回
    // `401 Unauthorized`
    // 状态码。
    if (token.isEmpty()) {
      // 这行代码返回状态为“401 Unauthorized”的“ServerResponse”对象。它用于指示用户无权访问所请求的资源。
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    // 这行代码使用 Auth0 JWT 库解码从 HTTP 请求标头中提取的 JWT
    // 令牌。然后它从解码的令牌中检索“id”声明并将其转换为整数值，该整数值分配给 userId 变量。此
    // `userId` 稍后在代码中用于执行特定于经过身份验证的用户的操作。
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    // `ObjectMapper mapper = new ObjectMapper();` 创建 `ObjectMapper` 类的新实例，它是
    // Jackson JSON
    // 处理库的一部分。该对象用于将 Java 对象转换为 JSON，反之亦然。在此代码中，`mapper` 对象用于根据从数据库检索的数据创建 JSON 对象
    // (`ObjectNode`)。
    ObjectMapper mapper = new ObjectMapper();
    // 此代码为按日期分组的用户待办事项记录生成效率数据，并在 ServerResponse 中将其作为 JSON 对象的 Flux 返回。
    Flux<ObjectNode> objectNodeFlux = service
        .getTodoRecordsByUserId(userId)
        .groupBy(todoRecord -> todoRecord.getCreateTime().toLocalDate())
        .flatMap(
            group -> group.reduce(
                (r1, r2) -> new TodoRecord(r1.getAmount() + r2.getAmount(), r1.getCreateTime())))
        .sort(Comparator.comparing(a -> a.getCreateTime().toLocalDate()))
        .map(
            todoRecord -> {
              // `ObjectNode node = mapper.createObjectNode();` 使用 Jackson `ObjectMapper`
              // 类创建一个新的
              // JSON 对象。 `ObjectMapper` 用于将 Java 对象转换为 JSON，反之亦然。在本例中，它用于创建一个新的 `ObjectNode`
              // JSON
              // 对象，该对象可用于构建 JSON 响应。
              ObjectNode node = mapper.createObjectNode();
              // `node.put("amount", todoRecord.getAmount());` 将键值对添加到 JSON 对象
              // `node`。键是“amount”，值是使用“getAmount()”方法检索的“todoRecord”对象的数量。这行代码用于创建一个 JSON
              // 对象，该对象表示特定日期的效率数据。
              node.put("amount", todoRecord.getAmount());
              // `node.put("time", todoRecord.getCreateTime().toLocalDate().toString());`
              // 将键值对添加到 JSON 对象 `node`。
              // 键是“time”，值是“todoRecord”对象的“createTime”属性的日期，使用“LocalDate”类的“toString()”方法转换为字符串。这用于创建表示特定日期的效率数据的
              // JSON 对象。
              node.put("time", todoRecord.getCreateTime().toLocalDate().toString());
              // `return node;` 返回一个使用 Jackson `ObjectMapper` 类创建的 JSON 对象 (`ObjectNode`)。
              // `node`
              // 对象包含两个键值对：“金额”和“时间”。 “金额”表示特定日期的效率数据，“时间”表示日期本身。此 JSON 对象用于表示响应正文中特定日期的效率数据。
              return node;
            });
    // 这行代码返回状态为“200 OK”的“ServerResponse”和包含“ObjectNode”JSON 对象的 Flux 的正文。
    // objectNodeFlux是将用户的todo记录按日期分组归约生成效率数据。 `ObjectMapper` 用于根据数据创建 `ObjectNode`
    // JSON
    // 对象，然后在响应正文中返回这些对象。
    return ServerResponse.ok().body(objectNodeFlux, ObjectNode.class);
  }
}
