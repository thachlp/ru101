package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class Purchase {
  @JsonProperty("order_id")
  private String orderId;
  @JsonProperty("customer")
  private Customer customer;
  @JsonProperty("tier")
  private String tier;
  @JsonProperty("quantity")
  private int quantity;
  @JsonProperty("cost")
  private double cost;
  @JsonProperty("sku")
  private String sku;
  @JsonProperty("created_at")
  private long createdAt;

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public void setTier(String tier) {
    this.tier = tier;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public String getOrderId() {
    return orderId;
  }

  public Customer getCustomer() {
    return customer;
  }

  public String getTier() {
    return tier;
  }

  public int getQuantity() {
    return quantity;
  }

  public double getCost() {
    return cost;
  }

  public String getSku() {
    return sku;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public Map<String, String> toMap() {
    final Map<String, String> values = new HashMap<>();
    values.put("order_id", getOrderId());
    values.put("customer", getCustomer().toString());
    values.put("tier", String.valueOf(getTier()));
    values.put("qty", String.valueOf(getQuantity()));
    values.put("cost", String.valueOf(getCost()));
    values.put("event_sku", getSku());
    values.put("created_at", String.valueOf(getCreatedAt()));
    return values;
  }
}
