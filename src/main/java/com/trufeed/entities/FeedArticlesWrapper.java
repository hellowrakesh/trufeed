package com.trufeed.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class FeedArticlesWrapper {

  private final String feedUuid;
  private final List<Article> articles;

  @JsonCreator
  public FeedArticlesWrapper(
      @JsonProperty("feedUuid") String feedUuid, @JsonProperty("articles") List<Article> articles) {
    this.feedUuid = feedUuid;
    this.articles = articles;
  }

  public String getFeedUuid() {
    return feedUuid;
  }

  public List<Article> getArticles() {
    return ImmutableList.copyOf(articles);
  }
}
