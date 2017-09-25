package com.trufeed.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.entities.Feed;
import com.trufeed.repository.FeedRepository;
import java.util.Date;

@Singleton
public class FeedService extends Service {

  private final FeedRepository repository;

  @Inject
  public FeedService(FeedRepository repository) {
    this.repository = repository;
  }

  public Task<Feed> get(String feedUuid) {
    return repository.get(feedUuid);
  }

  public Task<Feed> add(Feed feed) {
    String uuid = generateUuid(feed.getName());
    Feed newFeed =
        new Feed(uuid, feed.getName(), feed.getTitle(), feed.getDescription(), new Date());
    return repository.save(newFeed);
  }
}
