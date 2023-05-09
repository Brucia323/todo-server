package io.zcy.todo;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.ServerRequest;

public class Util {
  public static final RequestPredicate ACCEPT_JSON = accept(MediaType.APPLICATION_JSON);

  public static Optional<String> getTokenFrom(ServerRequest request) {
    String authorization = request.headers().header("Authorization").get(0);
    if (authorization != null && authorization.toLowerCase().startsWith("bearer ")) {
      return Optional.of(authorization.substring(7));
    }
    return Optional.empty();
  }
  
  public record TimeObject(String day, String beginTime, String endTime) {}
}
