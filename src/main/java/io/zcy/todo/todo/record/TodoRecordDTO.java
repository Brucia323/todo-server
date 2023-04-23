package io.zcy.todo.todo.record;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
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
}
