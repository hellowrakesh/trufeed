package com.trufeed.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Article {

  private final String uuid;
  private final String title;
  private final String description;
  private final String content;
  private final Map<String, Serializable> metadata;
  private final String author;
  private final Date publishedDate;

  @JsonCreator
  public Article(
      @JsonProperty("uuid") String uuid,
      @JsonProperty("title") String title,
      @JsonProperty("description") String description,
      @JsonProperty("content") String content,
      @JsonProperty("metadata") Map<String, Serializable> metadata,
      @JsonProperty("author") String author,
      @JsonProperty("publishedDate") Date publishedDate) {
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.content = content;
    this.metadata = metadata;
    this.author = author;
    this.publishedDate = publishedDate;
  }

  public String getUuid() {
    return uuid;
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

  public Date getPublishedDate() {
    return publishedDate;
  }
}
