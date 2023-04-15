package io.zcy.todo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class User {
  @Id private Integer id;
  private String name;
  private String email;
  @JsonIgnore private String passwordHash;
  private String timePerWeek;
  @CreatedDate private LocalDateTime createTime;
  @LastModifiedDate private LocalDateTime updateTime;

  public User() {}

  public User(String name, String email, String passwordHash) {
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
  }

  public User(
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

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
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
    return "User{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", email='"
        + email
        + '\''
        + ", passwordHash='"
        + passwordHash
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
