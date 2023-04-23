package io.zcy.todo.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("todos")
@Getter
@Setter
@ToString
public class Todo {
  @Id private Integer id;
  private Integer userId;
  private String name;
  private LocalDate beginTime;
  private LocalDate plannedEndTime;
  private LocalDate actualEndTime;
  private Integer currentAmount = 0;
  private Integer totalAmount;
  private String description;
  private LocalDateTime createTime = LocalDateTime.now();
  private LocalDateTime updateTime = LocalDateTime.now();

  public Todo() {}

  public Todo(
      Integer userId,
      String name,
      LocalDate beginTime,
      LocalDate plannedEndTime,
      Integer totalAmount,
      String description) {
    this.userId = userId;
    this.name = name;
    this.beginTime = beginTime;
    this.plannedEndTime = plannedEndTime;
    this.totalAmount = totalAmount;
    this.description = description;
  }

  public Todo(
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
}
