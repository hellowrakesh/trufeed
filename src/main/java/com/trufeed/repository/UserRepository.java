package com.trufeed.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.container.TrufeedConfiguration.Storage;
import com.trufeed.entities.User;
import java.util.List;

@Singleton
public class UserRepository extends FileRepository {

  private static final String USER_META_FILE_NAME = "user.meta";
  private static final String USER_FEED_DIR_NAME = "feeds";
  private static final String USER_FEED_FILE_NAME_FORMAT = "%s";

  @Inject
  public UserRepository(Storage storage) {
    super(storage, storage.getUserStore());
  }

  public Task<User> get(String uuid) {
    return getEntity(User.class, uuid, USER_META_FILE_NAME);
  }

  public Task<User> save(User user) {
    return createEmptyFile(user.getUuid(), USER_META_FILE_NAME)
        .flatMap(
            success -> {
              if (success) {
                return saveFileContents(user, user.getUuid(), USER_META_FILE_NAME);
              }
              throw new RuntimeException("User already exists");
            })
        .flatMap(
            s -> {
              if (s) {
                return Task.value(user);
              } else {
                throw new RuntimeException("Unable to save userName: " + user.getUserName());
              }
            });
  }

  public Task<List<String>> getAllFeeds(String userUuid) {
    return getAllFileNames(userUuid, USER_FEED_DIR_NAME);
  }

  public Task<Boolean> subscribe(String userUuid, String feedUuid) {
    return createEmptyFile(userUuid, USER_FEED_DIR_NAME, getFeedFileName(feedUuid));
  }

  public Task<Boolean> unsubscribe(String userUuid, String feedUuid) {
    return deleteFile(userUuid, USER_FEED_DIR_NAME, getFeedFileName(feedUuid));
  }

  private String getFeedFileName(String feedUuid) {
    return String.format(USER_FEED_FILE_NAME_FORMAT, feedUuid);
  }
}
