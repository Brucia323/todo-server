package io.zcy.todo.user;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {
  @Resource private UserRepository repository;

  public Mono<User> getUserById(Integer id) {
    return repository.findById(id);
  }

  public Mono<User> getUserByEmail(String email) {
    return repository.findByEmail(email);
  }

  public Mono<User> createUser(UserDTO userDTO) {
    return repository
        .findByEmail(userDTO.getEmail())
        .defaultIfEmpty(new User())
        .flatMap(
            user -> {
              if (user.getId() != null) {
                log.info("【{}】已注册", user.getEmail());
                return Mono.error(new RuntimeException("该邮箱已注册"));
              }
              String passwordHash = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt(10));
              user = new User(userDTO.getName(), userDTO.getEmail(), passwordHash);
              log.info("正在注册: {}", user);
              return repository.save(user);
            });
  }

  public Mono<User> updateUser(UserDTO userDTO) {
    return repository
        .findById(userDTO.getId())
        .map(
            user -> {
              log.info("【{}】正在更新信息", user.getId());
              String password = userDTO.getPassword();

              user.setName(userDTO.getName());
              user.setEmail(userDTO.getEmail());
              user.setTimePerWeek(userDTO.getTimePerWeek());
              user.setUpdateTime(LocalDateTime.now());

              if (password != null) {
                String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
                user.setPasswordHash(passwordHash);
              }

              return user;
            })
        .flatMap(repository::save);
  }

  public Mono<Void> deleteUser(Integer id) {
    return repository.deleteById(id);
  }

  public Mono<String> generateCaptcha() {
    double v = Math.random() * 9000 + 1000;
    long round = Math.round(v);
    log.info("验证码是【{}】", round);
    return Mono.just(String.valueOf(round));
  }
}
