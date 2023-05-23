package io.zcy.todo.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Todo {
  @Id
  private Integer id;
  private Integer userId;
  private String name;
  private LocalDate beginDate;
  private LocalDate plannedEndDate;
  private LocalDate actualEndDate;
  private Integer currentAmount = 0;
  private Integer totalAmount;
  private String description;
  private LocalDateTime createTime = LocalDateTime.now();
  private LocalDateTime updateTime = LocalDateTime.now();

  public Todo() {
  }

  public Todo(
      Integer userId,
      String name,
      LocalDate beginDate,
      LocalDate plannedEndDate,
      Integer totalAmount,
      String description) {
    this.userId = userId;
    this.name = name;
    this.beginDate = beginDate;
    this.plannedEndDate = plannedEndDate;
    this.totalAmount = totalAmount;
    this.description = description;
  }

  public Todo(
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
}
