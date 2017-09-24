package com.trufeed.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class Feed {

  private final String uuid;
  private final String name;
  private final Date createDate;

  @JsonCreator
  public Feed(
      @JsonProperty("uuid") String uuid,
      @JsonProperty("name") String name,
      @JsonProperty("createDate") Date createDate) {
    this.uuid = uuid;
    this.name = name;
    this.createDate = createDate;
  }

  public String getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public Date getCreateDate() {
    return createDate;
  }
}
