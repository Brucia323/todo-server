package io.zcy.todo.account;

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
public class AccountHandler {
  @Resource
  private AccountService service;

  public Mono<ServerResponse> login(ServerRequest request) {
    log.info("{}", request);
    return request
      .bodyToMono(AccountDTO.class)
      .flatMap(
        accountDTO -> {
          return service
            .getUserByEmail(accountDTO.getEmail())
            .flatMap(
              account -> {
                if (account == null) {
                  return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
                }
                boolean checkpw = BCrypt.checkpw(accountDTO.getPassword(), account.getPasswordHash());
                if (!checkpw) {
                  return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
                }
                String token = JWT.create()
                  .withClaim("id", account.getId())
                  .sign(Algorithm.HMAC256("todo"));
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("name", account.getName());
                node.put("email", account.getEmail());
                node.put("token", token);
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
    Mono<AccountDTO> userDTO = service.getUserById(userId).map(AccountDTO::new);
    return ServerResponse.ok().body(userDTO, AccountDTO.class);
  }

  public Mono<ServerResponse> getUserByEmail(ServerRequest request) {
    log.info("{}", request);
    String email = request.pathVariable("email");
    Mono<Account> account = service
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
    return ServerResponse.ok().body(account, Account.class);
  }

  public Mono<ServerResponse> createUser(ServerRequest request) {
    log.info("{}", request);
    Mono<AccountDTO> accountDTO = request.bodyToMono(AccountDTO.class).flatMap(service::createUser)
      .map(AccountDTO::new);
    return ServerResponse.status(HttpStatus.CREATED).body(accountDTO, AccountDTO.class);
  }

  public Mono<ServerResponse> updateUser(ServerRequest request) {
    log.info("{}", request);
    Mono<AccountDTO> accountDTO = request.bodyToMono(AccountDTO.class).flatMap(service::updateUser)
      .map(AccountDTO::new);
    return ServerResponse.ok().body(accountDTO, AccountDTO.class);
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

  public Mono<ServerResponse> checkPassword(ServerRequest request) {
    log.info("{}", request);
    Optional<String> token = getTokenFrom(request);
    if (token.isEmpty()) {
      return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
    Integer id = JWT.decode(token.get()).getClaim("id").asInt();
    Optional<String> password = request.queryParam("password");
    return password.map(s -> service.checkPassword(id, s).flatMap(result -> result ? ServerResponse.ok().build() : ServerResponse.status(HttpStatus.UNAUTHORIZED).build())).orElseGet(() -> ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
  }
}
