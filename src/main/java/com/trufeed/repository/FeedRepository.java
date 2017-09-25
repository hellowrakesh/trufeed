package com.trufeed.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.container.TrufeedConfiguration.Storage;
import com.trufeed.entities.Feed;

@Singleton
public class FeedRepository extends FileRepository {

  private static final String FEED_META_FILE_NAME = "feed.meta";

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
}
