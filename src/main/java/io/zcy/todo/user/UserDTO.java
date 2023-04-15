package io.zcy.todo.user;

import java.time.LocalDateTime;

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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getTimePerWeek() {
    return timePerWeek;
  }

  public void setTimePerWeek(String timePerWeek) {
    this.timePerWeek = timePerWeek;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public String toString() {
    return "UserDTO{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", email='"
        + email
        + '\''
        + ", password='"
        + password
        + '\''
        + ", timePerWeek='"
        + timePerWeek
        + '\''
        + ", createTime="
        + createTime
        + ", updateTime="
        + updateTime
        + '}';
  }
}
