package io.zcy.todo;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Optional;

public class Util {
  public static Optional<String> getTokenFrom(ServerRequest request) {
    String authorization =
        request.headers().header("Authorization").get(0);
    if (authorization!=null && authorization.toLowerCase().startsWith("bearer ")) {
      return Optional.of(authorization.substring(7));
    }
    return Optional.empty();
  }
}
