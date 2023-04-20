package io.zcy.todo.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {
  @Resource private UserService service;

  public Mono<ServerResponse> login(ServerRequest request) {
    return request
        .bodyToMono(UserDTO.class)
        .flatMap(
            userDTO ->
                service
                    .getUserByEmail(userDTO)
                    .flatMap(
                        user -> {
                          if (user == null) {
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
                          return ServerResponse.status(HttpStatus.CREATED).bodyValue(node);
                        }));
  }

  public Mono<ServerResponse> getUserById(ServerRequest request) {
    Integer id = Integer.valueOf(request.pathVariable("id"));
    System.out.println(id);
    Mono<UserDTO> userDTO = service.getUserById(id).map(UserDTO::new);
    return ServerResponse.ok().body(userDTO, UserDTO.class);
  }

  public Mono<ServerResponse> createUser(ServerRequest request) {
    Mono<UserDTO> userDTO =
        request.bodyToMono(UserDTO.class).flatMap(service::createUser).map(UserDTO::new);
    return ServerResponse.status(HttpStatus.CREATED).body(userDTO, UserDTO.class);
  }

  public Mono<ServerResponse> updateUser(ServerRequest request) {
    Mono<UserDTO> userDTO =
        request.bodyToMono(UserDTO.class).flatMap(service::updateUser).map(UserDTO::new);
    return ServerResponse.ok().body(userDTO, UserDTO.class);
  }

  public Mono<ServerResponse> deleteUser(ServerRequest request) {
    Integer id = Integer.valueOf(request.pathVariable("id"));
    return service.deleteUser(id).then(ServerResponse.noContent().build());
  }
}
