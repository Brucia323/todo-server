package io.zcy.todo.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserDTO {
  private Integer id;
  private String name;
  private String email;
  private String password;
  private String timePerWeek;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

  public UserDTO() {}

  public UserDTO(
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

  public UserDTO(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
    this.timePerWeek = user.getTimePerWeek();
    this.createTime = user.getCreateTime();
    this.updateTime = user.getUpdateTime();
  }
}
