package io.zcy.todo.account;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Account {
  @Id
  private Integer id;
  private String name;
  private String email;
  @JsonIgnore
  private String passwordHash;
  private String timePerWeek;

  private LocalDateTime createTime = LocalDateTime.now();

  private LocalDateTime updateTime = LocalDateTime.now();

  public Account() {
  }

  public Account(String name, String email, String passwordHash) {
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
  }

  public Account(
    Integer id,
    String name,
    String email,
    String passwordHash,
    String timePerWeek,
    LocalDateTime createTime,
    LocalDateTime updateTime) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
    this.timePerWeek = timePerWeek;
    this.createTime = createTime;
    this.updateTime = updateTime;
  }
}
