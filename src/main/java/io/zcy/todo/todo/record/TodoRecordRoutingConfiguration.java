package io.zcy.todo.todo.record;

import static io.zcy.todo.Util.ACCEPT_JSON;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class TodoRecordRoutingConfiguration {
  @Bean
  public RouterFunction<ServerResponse> TodoRecordRouterFunction(
      TodoRecordHandler todoRecordHandler) {
    return route()
        .POST("/todo/{id}", ACCEPT_JSON, todoRecordHandler::createTodoRecord)
        .GET("/efficiency", ACCEPT_JSON, todoRecordHandler::generateEfficiency)
        .build();
  }
}
