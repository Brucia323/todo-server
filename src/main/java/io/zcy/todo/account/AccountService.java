package io.zcy.todo.account;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AccountService {
  @Resource
  private AccountRepository repository;

  public Mono<Account> getUserById(Integer id) {
    return repository.findById(id);
  }

  public Mono<Account> getUserByEmail(String email) {
    return repository.findByEmail(email);
  }

  public Mono<Account> createUser(AccountDTO accountDTO) {
    return repository
      .findByEmail(accountDTO.getEmail())
      .defaultIfEmpty(new Account())
      .flatMap(
        account -> {
          if (account.getId() != null) {
            return Mono.error(new RuntimeException("该邮箱已注册"));
          }
          String passwordHash = BCrypt.hashpw(accountDTO.getPassword(), BCrypt.gensalt(10));
          account = new Account(accountDTO.getName(), accountDTO.getEmail(), passwordHash);
          return repository.save(account);
        });
  }

  public Mono<Account> updateUser(AccountDTO accountDTO) {
    return repository
      .findById(accountDTO.getId())
      .map(
        account -> {
          String password = accountDTO.getPassword();

          account.setName(accountDTO.getName());
          account.setEmail(accountDTO.getEmail());
          account.setTimePerWeek(accountDTO.getTimePerWeek());
          account.setUpdateTime(LocalDateTime.now());

          if (password != null) {
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            account.setPasswordHash(passwordHash);
          }

          return account;
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

  public Mono<Boolean> checkPassword(Integer id, String password) {
    return repository.findById(id).map(Account::getPasswordHash).map(passwordHash -> BCrypt.checkpw(password, passwordHash));
  }
}
