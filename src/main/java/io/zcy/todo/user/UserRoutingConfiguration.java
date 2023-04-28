package io.zcy.todo.user;

import static io.zcy.todo.Util.ACCEPT_JSON;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class UserRoutingConfiguration {
  @Bean
  public RouterFunction<ServerResponse> monoRouterFunction(UserHandler userHandler) {
    return route()
        .GET("/user", ACCEPT_JSON, userHandler::getUserById)
        .GET("/user/email/{email}", ACCEPT_JSON, userHandler::getUserByEmail)
        .GET("/user/captcha", ACCEPT_JSON, userHandler::generateCaptcha)
        .POST("/user", ACCEPT_JSON, userHandler::createUser)
        .PUT("/user/{id}", ACCEPT_JSON, userHandler::updateUser)
        .DELETE("/user/{id}", ACCEPT_JSON, userHandler::deleteUser)
        .POST("/login", ACCEPT_JSON, userHandler::login)
        .build();
  }
}
