package io.zcy.todo.todo.record;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table("todo_record")
public class TodoRecord {
  @Id private Integer id;
  private Integer userId;
  private Integer todoId;
  private Integer currentAmount;
  @CreatedDate private LocalDateTime createTime;
  @LastModifiedDate private LocalDateTime updateTime;

  public TodoRecord() {}

  public TodoRecord(Integer userId, Integer todoId, Integer currentAmount) {
    this.userId = userId;
    this.todoId = todoId;
    this.currentAmount = currentAmount;
  }

  public TodoRecord(
      Integer id,
      Integer userId,
      Integer todoId,
      Integer currentAmount,
      LocalDateTime createTime,
      LocalDateTime updateTime) {
    this.id = id;
    this.userId = userId;
    this.todoId = todoId;
    this.currentAmount = currentAmount;
    this.createTime = createTime;
    this.updateTime = updateTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getTodoId() {
    return todoId;
  }

  public void setTodoId(Integer todoId) {
    this.todoId = todoId;
  }

  public Integer getCurrentAmount() {
    return currentAmount;
  }

  public void setCurrentAmount(Integer currentAmount) {
    this.currentAmount = currentAmount;
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
    return "TodoRecord{"
        + "id="
        + id
        + ", userId="
        + userId
        + ", todoId="
        + todoId
        + ", currentAmount="
        + currentAmount
        + ", createTime="
        + createTime
        + ", updateTime="
        + updateTime
        + '}';
  }
}
