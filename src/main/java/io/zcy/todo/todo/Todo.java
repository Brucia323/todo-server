package io.zcy.todo.todo;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table("todos")
public class Todo {
  @Id private Integer id;
  private Integer userId;
  private String name;
  private LocalDateTime beginTime;
  private LocalDateTime plannedEndTime;
  private LocalDateTime actualEndTime;
  private Integer currentAmount;
  private Integer totalAmount;
  private String description;
  @CreatedDate private LocalDateTime createTime;
  @LastModifiedDate private LocalDateTime updateTime;

  public Todo() {}

  public Todo(
      Integer userId,
      String name,
      LocalDateTime beginTime,
      LocalDateTime plannedEndTime,
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
      LocalDateTime beginTime,
      LocalDateTime plannedEndTime,
      LocalDateTime actualEndTime,
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDateTime getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(LocalDateTime beginTime) {
    this.beginTime = beginTime;
  }

  public LocalDateTime getPlannedEndTime() {
    return plannedEndTime;
  }

  public void setPlannedEndTime(LocalDateTime plannedEndTime) {
    this.plannedEndTime = plannedEndTime;
  }

  public LocalDateTime getActualEndTime() {
    return actualEndTime;
  }

  public void setActualEndTime(LocalDateTime actualEndTime) {
    this.actualEndTime = actualEndTime;
  }

  public Integer getCurrentAmount() {
    return currentAmount;
  }

  public void setCurrentAmount(Integer currentAmount) {
    this.currentAmount = currentAmount;
  }

  public Integer getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Integer totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
    return "Todo{"
        + "id="
        + id
        + ", userId="
        + userId
        + ", name='"
        + name
        + '\''
        + ", beginTime="
        + beginTime
        + ", plannedEndTime="
        + plannedEndTime
        + ", actualEndTime="
        + actualEndTime
        + ", currentAmount="
        + currentAmount
        + ", totalAmount="
        + totalAmount
        + ", description='"
        + description
        + '\''
        + ", createTime="
        + createTime
        + ", updateTime="
        + updateTime
        + '}';
  }
}
