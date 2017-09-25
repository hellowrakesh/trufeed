package com.trufeed.repository;

import static com.trufeed.utils.CommonUtils.fromJsonStringToObject;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.container.TrufeedConfiguration.Storage;
import com.trufeed.entities.Article;
import com.trufeed.entities.Feed;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class FeedRepository extends FileRepository {

  private static final String FEED_META_FILE_NAME = "feed.meta";
  private static final String FEED_ARTICLE_DIR_NAME = "articles";
  private static final String FEED_ARTICLE_DEFAULT_FILE_NAME = "_part0";

  @Inject
  public FeedRepository(Storage storage) {
    super(storage, storage.getFeedStore());
  }

  public Task<Feed> get(String uuid) {
    return getEntity(Feed.class, uuid, FEED_META_FILE_NAME);
  }

  public Task<Feed> save(Feed feed) {
    return createEmptyFile(feed.getUuid(), FEED_META_FILE_NAME)
        .flatMap(
            success -> {
              if (success) {
                return saveFileContents(feed, feed.getUuid(), FEED_META_FILE_NAME);
              }
              throw new RuntimeException("Feed already exists");
            })
        .flatMap(
            s -> {
              if (s) {
                return Task.value(feed);
              } else {
                throw new RuntimeException("Unable to save feed: " + feed.getName());
              }
            });
  }

  public Task<List<Article>> getArticles(String feedUuid) {
    return getFileContentsAsStream(feedUuid, FEED_ARTICLE_DIR_NAME, FEED_ARTICLE_DEFAULT_FILE_NAME)
        .flatMap(
            stream -> {
              // sort the stream in reverse order to have the latest
              // element at the top
              return Task.value(
                  stream
                      .sorted(Collections.reverseOrder())
                      .map(line -> fromJsonStringToObject(line, Article.class))
                      .collect(Collectors.toList()));
            });
  }

  public Task<Boolean> saveArticle(String feedUuid, List<Article> articles) {

    Task<Boolean> saveArticlesTask =
        saveFileContents(
            Lists.newArrayList(articles),
            feedUuid,
            FEED_ARTICLE_DIR_NAME,
            FEED_ARTICLE_DEFAULT_FILE_NAME);
    return exists(feedUuid, FEED_ARTICLE_DIR_NAME, FEED_ARTICLE_DEFAULT_FILE_NAME)
        .flatMap(
            exist -> {
              if (!exist) {
                return createEmptyFile(
                        feedUuid, FEED_ARTICLE_DIR_NAME, FEED_ARTICLE_DEFAULT_FILE_NAME)
                    .flatMap(
                        created -> {
                          if (created) {
                            return saveArticlesTask;
                          } else {
                            throw new RuntimeException(
                                "Error while saving the article for feed: " + feedUuid);
                          }
                        });
              } else {
                return saveArticlesTask;
              }
            });
  }
}
