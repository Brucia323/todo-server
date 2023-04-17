package io.zcy.todo.todo;

import static io.zcy.todo.Util.ACCEPT_JSON;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class TodoRoutingConfiguration {
  @Bean
  public RouterFunction<ServerResponse> monoRouterFunction(TodoHandler todoHandler) {
    return route()
        .GET("/todo", ACCEPT_JSON, todoHandler::getTodos)
        .GET("/todo/{id}", ACCEPT_JSON, todoHandler::getTodoById)
        .POST("/todo", ACCEPT_JSON, todoHandler::createTodo)
        .PUT("/todo/{id}", ACCEPT_JSON, todoHandler::updateTodo)
        .DELETE("/todo/{id}", ACCEPT_JSON, todoHandler::deleteTodo)
        .build();
  }
}
