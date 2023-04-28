package io.zcy.todo.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static io.zcy.todo.Util.getTokenFrom;

@Component
@Slf4j
public class UserHandler {
  @Resource private UserService service;

  public Mono<ServerResponse> login(ServerRequest request) {
    log.info("{}", request);
    return request
        .bodyToMono(UserDTO.class)
        .flatMap(
            userDTO -> {
              log.info("【{}】请求登录", userDTO.getEmail());
              return service
                  .getUserByEmail(userDTO.getEmail())
                  .flatMap(
                      user -> {
                        if (user == null) {
                          log.info("【{}】未注册", userDTO.getEmail());
                          return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
                        }
                        boolean checkpw =
                            BCrypt.checkpw(userDTO.getPassword(), user.getPasswordHash());
                        if (!checkpw) {
                          return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
                        }
                        String token =
                            JWT.create()
                                .withClaim("id", user.getId())
                                .sign(Algorithm.HMAC256("todo"));
                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode node = mapper.createObjectNode();
                        node.put("name", user.getName());
                        node.put("email", user.getEmail());
                        node.put("token", token);
                        log.info("【{}】正在登录", user.getId());
                        return ServerResponse.status(HttpStatus.CREATED).bodyValue(node);
                      });
            });
  }

  public Mono<ServerResponse> getUserById(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer userId = JWT.decode(token.get()).getClaim("id").asInt();
    Mono<UserDTO> userDTO = service.getUserById(userId).map(UserDTO::new);
    return ServerResponse.ok().body(userDTO, UserDTO.class);
  }

  public Mono<ServerResponse> getUserByEmail(ServerRequest request) {
    log.info("{}", request);
    String email = request.pathVariable("email");
    Mono<User> user =
        service
            .getUserByEmail(email)
            .singleOptional()
            .handle(
                (optional, sink) -> {
                  if (optional.isEmpty()) {
                    sink.error(new RuntimeException("该邮箱未注册"));
                    return;
                  }
                  sink.next(optional.get());
                });
    return ServerResponse.ok().body(user, User.class);
  }

  public Mono<ServerResponse> createUser(ServerRequest request) {
    log.info("{}", request);
    Mono<UserDTO> userDTO =
        request.bodyToMono(UserDTO.class).flatMap(service::createUser).map(UserDTO::new);
    return ServerResponse.status(HttpStatus.CREATED).body(userDTO, UserDTO.class);
  }

  public Mono<ServerResponse> updateUser(ServerRequest request) {
    log.info("{}", request);
    Mono<UserDTO> userDTO =
        request.bodyToMono(UserDTO.class).flatMap(service::updateUser).map(UserDTO::new);
    return ServerResponse.ok().body(userDTO, UserDTO.class);
  }

  public Mono<ServerResponse> deleteUser(ServerRequest request) {
    log.info("{}", request);
    Integer id = Integer.valueOf(request.pathVariable("id"));
    return service.deleteUser(id).then(ServerResponse.noContent().build());
  }

  public Mono<ServerResponse> generateCaptcha(ServerRequest request) {
    log.info("{}", request);
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node = mapper.createObjectNode();
    service.generateCaptcha().map(captcha -> node.put("captcha", captcha)).subscribe();
    return ServerResponse.ok().bodyValue(node);
  }
}
