package io.zcy.todo.account;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.zcy.todo.Util.ACCEPT_JSON;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration(proxyBeanMethods = false)
public class AccountRoutingConfiguration {
  @Bean
  public RouterFunction<ServerResponse> monoRouterFunction(AccountHandler accountHandler) {
    return route()
      .GET("/user", ACCEPT_JSON, accountHandler::getUserById)
      .GET("/user/email/{email}", ACCEPT_JSON, accountHandler::getUserByEmail)
      .GET("/user/captcha", ACCEPT_JSON, accountHandler::generateCaptcha)
      .GET("/password", RequestPredicates.queryParam("password", t -> true), accountHandler::checkPassword)
      .POST("/user", ACCEPT_JSON, accountHandler::createUser)
      .PUT("/user/{id}", ACCEPT_JSON, accountHandler::updateUser)
      .DELETE("/user/{id}", ACCEPT_JSON, accountHandler::deleteUser)
      .POST("/login", ACCEPT_JSON, accountHandler::login)
      .build();
  }
}
