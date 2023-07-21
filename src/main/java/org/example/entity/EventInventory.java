package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class EventInventory extends Event {
  @JsonProperty("capacity")
  private int capacity;

  @JsonProperty("available:General")
  private int available;

  @JsonProperty("price:General")
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
    final Map<String, String> values = new HashMap<>();
    values.put("sku", getSku());
    values.put("name", getName());
    values.put("disabled_access", String.valueOf(isDisabledAccess()));
    values.put("medal_event", String.valueOf(isMedalEvent()));
    values.put("venue", getVenue());
    values.put("category", String.valueOf(getCategory()));
    values.put("capacity", String.valueOf(getCapacity()));
    values.put(String.format("available:%s", tier), String.valueOf(getAvailable()));
    values.put(String.format("price:%s", tier), String.valueOf(getPrice()));
    return values;
  }
}
