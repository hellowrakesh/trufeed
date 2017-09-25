package com.trufeed.service;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.entities.Article;
import com.trufeed.entities.Feed;
import com.trufeed.repository.FeedRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FeedService extends Service {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final FeedRepository repository;

  @Inject
  public FeedService(FeedRepository repository) {
    this.repository = repository;
    LOG.info("Initialized feed service");
  }

  public Task<Feed> get(String feedUuid) {
    return repository.get(feedUuid);
  }

  public Task<List<Feed>> getAll() {
    return repository.getAll();
  }

  public Task<Feed> add(Feed feed) {
    String uuid = generateUuid(feed.getName());
    Feed newFeed =
        new Feed(uuid, feed.getName(), feed.getTitle(), feed.getDescription(), new Date());
    return repository.save(newFeed);
  }

  public Task<List<Article>> getArticles(String feedUuid) {
    return repository.getArticles(feedUuid);
  }

  public Task<List<Article>> publishArticles(String feedUuid, List<Article> articleList) {
    List<Article> newArticles =
        articleList
            .stream()
            .map(
                article ->
                    new Article(
                        generateRandomUuid(),
                        article.getTitle(),
                        article.getDescription(),
                        article.getContent(),
                        article.getMetadata(),
                        article.getAuthor(),
                        new Date()))
            .collect(Collectors.toList());
    return repository
        .saveArticle(feedUuid, newArticles)
        .flatMap(
            success -> {
              if (success) {
                return Task.value(newArticles);
              } else {
                throw new RuntimeException("Error while publishing the article");
              }
            });
  }

  public Task<Article> publishArticle(String feedUuid, Article article) {
    Article newArticle =
        new Article(
            generateRandomUuid(),
            article.getTitle(),
            article.getDescription(),
            article.getContent(),
            article.getMetadata(),
            article.getAuthor(),
            new Date());
    return repository
        .saveArticle(feedUuid, Lists.newArrayList(newArticle))
        .flatMap(
            success -> {
              if (success) {
                return Task.value(newArticle);
              } else {
                throw new RuntimeException("Error while publishing the article");
              }
            });
  }
}
