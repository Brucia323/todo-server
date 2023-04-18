package io.zcy.todo.todo.record;

import java.time.LocalDateTime;

public class TodoRecordDTO {
  private Integer id;
  private Integer currentAmount;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

  public TodoRecordDTO() {}

  public TodoRecordDTO(
      Integer id, Integer currentAmount, LocalDateTime createTime, LocalDateTime updateTime) {
    this.id = id;
    this.currentAmount = currentAmount;
    this.createTime = createTime;
    this.updateTime = updateTime;
  }

  public TodoRecordDTO(TodoRecord todoRecord) {
    this.id = todoRecord.getId();
    this.currentAmount = todoRecord.getCurrentAmount();
    this.createTime = todoRecord.getCreateTime();
    this.updateTime = todoRecord.getUpdateTime();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
    return "TodoRecordDTO{"
        + "id="
        + id
        + ", currentAmount="
        + currentAmount
        + ", createTime="
        + createTime
        + ", updateTime="
        + updateTime
        + '}';
  }
}
