package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class Customer {
  @JsonProperty("id")
  private String id;
  @JsonProperty("customer_name")
  private String customerName;
  public String getId() {
    return id;
  }

  public String getCustomerName() {
    return customerName;
  }

  public Map<String, String> toMap() {
    Map<String, String> values = new HashMap<>();
    values.put("id", this.getId());
    values.put("customer_name", this.getCustomerName());
    return values;
  }
}
