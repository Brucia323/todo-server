package io.zcy.todo.todo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TodoDTO {
  private Integer id;
  private Integer userId;
  private String name;
  private LocalDate beginTime;
  private LocalDate plannedEndTime;
  private LocalDate actualEndTime;
  private Integer currentAmount;
  private Integer totalAmount;
  private String description;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

  public TodoDTO() {}

  public TodoDTO(
      Integer id,
      Integer userId,
      String name,
      LocalDate beginTime,
      LocalDate plannedEndTime,
      LocalDate actualEndTime,
      Integer currentAmount,
      Integer totalAmount,
      String description,
      LocalDateTime createTime,
      LocalDateTime updateTime) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.beginTime = beginTime;
    this.plannedEndTime = plannedEndTime;
    this.actualEndTime = actualEndTime;
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
    this.beginTime = todo.getBeginTime();
    this.plannedEndTime = todo.getPlannedEndTime();
    this.actualEndTime = todo.getActualEndTime();
    this.currentAmount = todo.getCurrentAmount();
    this.totalAmount = todo.getTotalAmount();
    this.description = todo.getDescription();
    this.createTime = todo.getCreateTime();
    this.updateTime = todo.getUpdateTime();
  }
}
