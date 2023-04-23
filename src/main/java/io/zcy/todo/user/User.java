package io.zcy.todo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Getter
@Setter
@ToString
public class User {
  @Id private Integer id;
  private String name;
  private String email;
  @JsonIgnore private String passwordHash;
  private String timePerWeek;

  private LocalDateTime createTime = LocalDateTime.now();

  private LocalDateTime updateTime = LocalDateTime.now();

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
}
