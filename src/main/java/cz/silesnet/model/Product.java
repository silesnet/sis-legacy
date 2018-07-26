package cz.silesnet.model;

import java.util.Date;

public class Product extends Entity {
  private String name;
  private Integer price;
  private String channel;
  private Integer position;
  private String country;
  private Date activeFrom;
  private Date activeTo;
  private Boolean canChangePrice;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Date getActiveFrom() {
    return activeFrom;
  }

  public void setActiveFrom(Date activeFrom) {
    this.activeFrom = activeFrom;
  }

  public Date getActiveTo() {
    return activeTo;
  }

  public void setActiveTo(Date activeTo) {
    this.activeTo = activeTo;
  }

  public Boolean getCanChangePrice() {
    return canChangePrice;
  }

  public void setCanChangePrice(Boolean canChangePrice) {
    this.canChangePrice = canChangePrice;
  }
}
