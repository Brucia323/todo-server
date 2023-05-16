package io.zcy.todo.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TodoDTO {
  private Integer id;
  private Integer userId;
  private String name;
  private LocalDate beginDate;
  private LocalDate plannedEndDate;
  private LocalDate actualEndDate;
  private Integer currentAmount;
  private Integer totalAmount;
  private String description;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

  public TodoDTO() {
  }

  public TodoDTO(
      Integer id,
      Integer userId,
      String name,
      LocalDate beginDate,
      LocalDate plannedEndDate,
      LocalDate actualEndDate,
      Integer currentAmount,
      Integer totalAmount,
      String description,
      LocalDateTime createTime,
      LocalDateTime updateTime) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.beginDate = beginDate;
    this.plannedEndDate = plannedEndDate;
    this.actualEndDate = actualEndDate;
    this.currentAmount = currentAmount;
    this.totalAmount = totalAmount;
    this.description = description;
    this.createTime = createTime;
    this.updateTime = updateTime;
  }

  public TodoDTO(Todo todo) {
    this.id = todo.getId();
    this.userId = todo.getUserId();
    this.name = todo.getName();
    this.beginDate = todo.getBeginDate();
    this.plannedEndDate = todo.getPlannedEndDate();
    this.actualEndDate = todo.getActualEndDate();
    this.currentAmount = todo.getCurrentAmount();
    this.totalAmount = todo.getTotalAmount();
    this.description = todo.getDescription();
    this.createTime = todo.getCreateTime();
    this.updateTime = todo.getUpdateTime();
  }
}
