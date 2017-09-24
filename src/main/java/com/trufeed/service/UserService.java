package com.trufeed.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.entities.User;
import com.trufeed.repository.UserRepository;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class UserService extends Service {

  private final UserRepository repository;

  @Inject
  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public Task<User> get(String userUuid) {
    return repository.getEntity(User.class, userUuid);
  }

  public Task<User> add(User user) {
    String uuid = generateNewUuid(user.getUserName());
    User newUser =
        new User(uuid, user.getUserName(), user.getFirstName(), user.getLastName(), new Date());
    return repository.save(newUser);
  }

  public Task<User> update(User user) {
    String uuid = generateNewUuid(user.getUserName());
    if (!StringUtils.equals(user.getUuid(), uuid)) {
      throw new RuntimeException("Invalid uuid or userName provided");
    }
    return repository
        .exists(uuid)
        .flatMap(
            success -> {
              if (success) {
                return repository.save(user);
              }
              throw new RuntimeException("No user with userName or uuid exists");
            });
  }

  //  public Task<List<Feed>> getSubscribedFeeds() {
  //
  //  }

  public Task<Boolean> subscribe(String userUuid, String feedUuid) {
    return repository
        .exists(userUuid)
        .flatMap(
            success -> {
              if (success) {
                String feedFileName = String.format("%s.feed", feedUuid);
                return repository.createEmptyFile(userUuid, feedFileName);
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
                String feedFileName = String.format("%s.feed", feedUuid);
                return repository.deleteFile(userUuid, feedFileName);
              }
              throw new RuntimeException("No user with userName or uuid exists");
            });
  }
}
