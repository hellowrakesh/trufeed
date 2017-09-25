package com.trufeed.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Date;

@JsonPropertyOrder({"createDate", "uuid"})
public abstract class LogSerializable implements Serializable {

  private static final long serialVersionUID = 1L;
  protected final Date createDate;
  protected final String uuid;

  public LogSerializable(Date createDate, String uuid) {
    this.createDate = createDate;
    this.uuid = uuid;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public String getUuid() {
    return uuid;
  }
}
