package io.zcy.todo.user;

import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class UserService {
  @Resource private UserRepository repository;

  public Mono<User> getUserById(Integer id) {
    return repository.findById(id);
  }

  public Mono<User> getUserByEmail(UserDTO userDTO) {
    return repository.findByEmail(userDTO.getEmail());
  }

  public Mono<User> createUser(UserDTO userDTO) {
    return repository
        .findByEmail(userDTO.getEmail())
        .defaultIfEmpty(new User())
        .flatMap(
            user -> {
              if (user.getId() != null) {
                return Mono.error(new RuntimeException(user.toString()));
              }
              String passwordHash = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt(10));
              user = new User(userDTO.getName(), userDTO.getEmail(), passwordHash);
              return repository.save(user);
            });
  }

  public Mono<User> updateUser(UserDTO userDTO) {
    return repository
        .findById(userDTO.getId())
        .map(
            user -> {
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
}
