package cz.silesnet.model;

public class Product extends Entity {
  private String name;
  private Integer downlink;
  private Integer uplink;
  private Integer price;
  private String channel;
  private Boolean isDedicated;
  private Integer priority;
  private String country;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getDownlink() {
    return downlink;
  }

  public void setDownlink(Integer downlink) {
    this.downlink = downlink;
  }

  public Integer getUplink() {
    return uplink;
  }

  public void setUplink(Integer uplink) {
    this.uplink = uplink;
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

  public Boolean getIsDedicated() {
    return isDedicated;
  }

  public void setIsDedicated(Boolean dedicated) {
    isDedicated = dedicated;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
