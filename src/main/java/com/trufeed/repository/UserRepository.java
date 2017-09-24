package com.trufeed.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Task;
import com.trufeed.container.TrufeedConfiguration.Storage;
import com.trufeed.entities.User;

@Singleton
public class UserRepository extends FileRepository {

  @Inject
  public UserRepository(Storage storage) {
    super(storage, storage.getUserStore());
  }

  public Task<User> save(User user) {
    return saveFileContents(user, user.getUuid())
        .flatMap(
            s -> {
              if (s) {
                return Task.value(user);
              }
              throw new RuntimeException("Unable to save userName: " + user.getUserName());
            });
  }

  public Task<Boolean> subscribe(String userUuid, String feedUuid) {
    return exists(userUuid)
        .flatMap(
            success -> {
              if (success) {
                String feedFileName = String.format("%s.feed", feedUuid);
                return createEmptyFile(userUuid, feedFileName);
              }
              throw new RuntimeException("No user with userName or uuid exists");
            });
  }

  public Task<Boolean> unsubscribe(String userUuid, String feedUuid) {
    return exists(userUuid)
        .flatMap(
            success -> {
              if (success) {
                String feedFileName = String.format("%s.feed", feedUuid);
                return deleteFile(userUuid, feedFileName);
              }
              throw new RuntimeException("No user with userName or uuid exists");
            });
  }
}
