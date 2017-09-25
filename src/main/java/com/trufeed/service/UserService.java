package com.trufeed.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.entities.Feed;
import com.trufeed.entities.User;
import com.trufeed.repository.UserRepository;

@Singleton
public class UserService extends Service {

  private final UserRepository repository;
  private final FeedService feedService;

  @Inject
  public UserService(UserRepository repository, FeedService feedService) {
    this.repository = repository;
    this.feedService = feedService;
  }

  public Task<User> get(String userUuid) {
    return repository.get(userUuid);
  }

  public Task<User> add(User user) {
    String uuid = generateUuid(user.getUserName());
    User newUser =
        new User(uuid, user.getUserName(), user.getFirstName(), user.getLastName(), new Date());
    return repository.save(newUser);
  }

  public Task<List<Feed>> getSubscribedFeeds(String userUuid) {
	  return repository.getAllFeeds(userUuid)
			  .flatMap(feeds -> Task.par(feeds.stream()
					  .map(feed -> feedService.get(feed))
					  .collect(Collectors.toList()))
					  );
  }

  public Task<Boolean> subscribe(String userUuid, String feedUuid) {
    return repository
        .exists(userUuid)
        .flatMap(
            success -> {
              if (success) {
                return repository.subscribe(userUuid, feedUuid);
              }
              throw new RuntimeException("No user with userName or uuid exists");
            });
  }

  public Task<Boolean> unsubscribe(String userUuid, String feedUuid) {
    return repository
        .exists(userUuid)
        .flatMap(
            success -> {
              if (success) {
                return repository.unsubscribe(userUuid, feedUuid);
              }
              throw new RuntimeException("No user with userName or uuid exists");
            });
  }
}
