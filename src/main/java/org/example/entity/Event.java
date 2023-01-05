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

  public String getName() {
    return name;
  }

  public boolean isDisabledAccess() {
    return disabledAccess;
  }

  public boolean isMedalEvent() {
    return medalEvent;
  }

  public String getVenue() {
    return venue;
  }

  public String getCategory() {
    return category;
  }
}
