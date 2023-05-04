package io.zcy.todo.todo.record;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
public class TodoRecord {
  @Id private Integer id;
  private Integer userId;
  private Integer todoId;
  private Integer amount;
  private LocalDateTime createTime = LocalDateTime.now();
  private LocalDateTime updateTime = LocalDateTime.now();

  public TodoRecord() {}

  public TodoRecord(Integer userId, Integer todoId, Integer amount) {
    this.userId = userId;
    this.todoId = todoId;
    this.amount = amount;
  }

  public TodoRecord(Integer amount, LocalDateTime createTime) {
    this.amount = amount;
    this.createTime = createTime;
  }

  public TodoRecord(
      Integer id,
      Integer userId,
      Integer todoId,
      Integer amount,
      LocalDateTime createTime,
      LocalDateTime updateTime) {
    this.id = id;
    this.userId = userId;
    this.todoId = todoId;
    this.amount = amount;
    this.createTime = createTime;
    this.updateTime = updateTime;
  }
}
