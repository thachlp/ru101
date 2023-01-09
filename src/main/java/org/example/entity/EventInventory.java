package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class EventInventory extends Event {
  @JsonProperty("capacity")
  private int capacity;

  @JsonProperty("available")
  private int available;

  @JsonProperty("price")
  private double price;

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public void setAvailable(int available) {
    this.available = available;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getAvailable() {
    return available;
  }

  public double getPrice() {
    return price;
  }

  public Map<String, String> toMap(String tier) {
    Map<String, String> values = new HashMap<>();
    values.put("sku", this.getSku());
    values.put("name", this.getName());
    values.put("disabled_access", String.valueOf(this.isDisabledAccess()));
    values.put("medal_event", String.valueOf(this.isMedalEvent()));
    values.put("venue", this.getVenue());
    values.put("category", String.valueOf(this.getCategory()));
    values.put("capacity", String.valueOf(this.getCapacity()));
    values.put(String.format("available:%s", tier), String.valueOf(this.getAvailable()));
    values.put(String.format("price:%s", tier), String.valueOf(this.getPrice()));
    return values;
  }
}
