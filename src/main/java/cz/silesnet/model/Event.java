package cz.silesnet.model;

import java.util.Date;

public class Event extends Entity {
  private String event;
  private String entity;
  private long entityId;
  private String data;
  private long commandId;
  private Date happenedOn;

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
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

  public long getCommandId() {
    return commandId;
  }

  public void setCommandId(long commandId) {
    this.commandId = commandId;
  }

  public Date getHappenedOn() {
    return happenedOn;
  }

  public void setHappenedOn(Date happenedOn) {
    this.happenedOn = happenedOn;
  }
}
