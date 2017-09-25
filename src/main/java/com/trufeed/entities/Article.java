package com.trufeed.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import org.hibernate.validator.constraints.NotEmpty;

public class Article extends FileSerializable {

  /** */
  private static final long serialVersionUID = 1L;

  @NotEmpty private final String title;
  private final String description;
  @NotEmpty private final String content;
  private final Map<String, Serializable> metadata;
  @NotEmpty private final String author;

  @JsonCreator
  public Article(
      @JsonProperty("uuid") String uuid,
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("content") String content,
      @JsonProperty("metadata") Map<String, Serializable> metadata,
      @JsonProperty("author") String author,
      @JsonProperty("createDate") Date createDate) {
    super(createDate, uuid);
    this.title = title;
    this.description = description;
    this.content = content;
    this.metadata = metadata;
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getContent() {
    return content;
  }

  public Map<String, Serializable> getMetadata() {
    return metadata;
  }

  public String getAuthor() {
    return author;
  }
}
