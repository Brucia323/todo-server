package io.zcy.todo.account;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class AccountDTO {
  private Integer id;
  private String name;
  private String email;
  private String password;
  private String timePerWeek;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

  public AccountDTO() {}

  public AccountDTO(
      Integer id,
      String name,
      String email,
      String password,
      String timePerWeek,
      LocalDateTime createTime,
      LocalDateTime updateTime) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.timePerWeek = timePerWeek;
    this.createTime = createTime;
    this.updateTime = updateTime;
  }

  public AccountDTO(Account account) {
    this.id = account.getId();
    this.name = account.getName();
    this.email = account.getEmail();
    this.timePerWeek = account.getTimePerWeek();
    this.createTime = account.getCreateTime();
    this.updateTime = account.getUpdateTime();
  }
}
