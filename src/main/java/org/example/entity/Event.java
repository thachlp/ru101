package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
  @JsonProperty("sku")
  private String sku;
  @JsonProperty("name")
  private String name;

  @JsonProperty("disabled_access")
  private boolean disabledAccess;
  @JsonProperty("medal_event")
  private boolean medalEvent;
  @JsonProperty("venue")
  private String venue;
  @JsonProperty("category")
  private String category;

  public String getSku() {
    return sku;
  }

  public String lookup(String lookup) {
    switch (lookup) {
      case "sku" -> {
        return sku;
      }
      case "name" -> {
        return name;
      }
      case "disabled_access" -> {
        return String.valueOf(disabledAccess);
      }
      case "medal_event" -> {
        return String.valueOf(medalEvent);
      }
      case "venue" -> {
        return venue;
      }
      case "category" -> {
        return category;
      }
      default -> {
        return "";
      }
    }
  }
}
