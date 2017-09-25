package com.trufeed.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;

public class Feed extends FileSerializable {

  /** */
  private static final long serialVersionUID = 1L;

  @NotEmpty private final String name;
  private final String title;
  private final String description;

  @JsonCreator
  public Feed(
      @JsonProperty("uuid") String uuid,
      @JsonProperty("name") String name,
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("createDate") Date createDate) {
    super(createDate, uuid);
    this.name = name;
    this.title = title;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("uuid", uuid)
        .add("name", name)
        .add("title", title)
        .add("description", description)
        .add("createDate", createDate)
        .toString();
  }
}
