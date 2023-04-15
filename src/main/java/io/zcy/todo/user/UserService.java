package io.zcy.todo.user;

import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
  @Resource private UserRepository repository;

  public Mono<UserDTO> getUserById(UserDTO userDTO) {
    return repository.findById(userDTO.getId()).map(UserDTO::new);
  }

  public Mono<UserDTO> createUser(UserDTO userDTO) {
    return repository
        .findByEmail(userDTO.getEmail())
        .map(Optional::ofNullable)
        .flatMap(
            optional -> {
              if (optional.isPresent()) {
                User user = optional.get();
                return Mono.just(user);
              }
              String password = userDTO.getPassword();
              String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
              User user = new User(userDTO.getName(), userDTO.getEmail(), passwordHash);
              return repository.save(user);
            })
        .map(UserDTO::new);
  }

  public Mono<UserDTO> updateUser(UserDTO userDTO) {
    return repository
        .findById(userDTO.getId())
        .map(
            user -> {
              String name = userDTO.getName();
              String email = userDTO.getEmail();
              String password = userDTO.getPassword();
              String timePerWeek = userDTO.getTimePerWeek();
              if (name != null) {
                user.setName(name);
              }
              if (email != null) {
                user.setEmail(email);
              }
              if (password != null) {
                String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
                user.setPasswordHash(passwordHash);
              }
              if (timePerWeek != null) {
                user.setTimePerWeek(timePerWeek);
              }
              return user;
            })
        .flatMap(repository::save)
        .map(UserDTO::new);
  }

  public Mono<Void> deleteUser(UserDTO userDTO) {
    return repository.deleteById(userDTO.getId());
  }
}
