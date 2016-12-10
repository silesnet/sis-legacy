package cz.silesnet.model;

import java.util.Date;

public class Command extends Entity {
  private String command;
  private String entity;
  private long entityId;
  private String data;
  private String status;
  private Date insertedOn;
  private Date startedOn;
  private Date finishedOn;

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public long getEntityId() {
    return entityId;
  }

  public void setEntityId(long entityId) {
    this.entityId = entityId;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getInsertedOn() {
    return insertedOn;
  }

  public void setInsertedOn(Date insertedOn) {
    this.insertedOn = insertedOn;
  }

  public Date getStartedOn() {
    return startedOn;
  }

  public void setStartedOn(Date startedOn) {
    this.startedOn = startedOn;
  }

  public Date getFinishedOn() {
    return finishedOn;
  }

  public void setFinishedOn(Date finishedOn) {
    this.finishedOn = finishedOn;
  }
}
