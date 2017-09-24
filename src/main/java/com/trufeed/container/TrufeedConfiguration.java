package com.trufeed.container;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Singleton;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

@Singleton
public class TrufeedConfiguration extends Configuration {

  private String template;
  private Storage storage;

  @JsonProperty
  public String getTemplate() {
    return template;
  }

  @JsonProperty
  public void setTemplate(String template) {
    this.template = template;
  }

  @JsonProperty("storage")
  public Storage getStorage() {
    return storage;
  }

  @JsonProperty("storage")
  public void setStorage(Storage storage) {
    this.storage = storage;
  }

  public static class Storage {
    @NotEmpty private String rootDir;
    @NotEmpty private Store userStore;
    @NotEmpty private Store feedStore;

    @JsonProperty("rootDir")
    public String getRootDir() {
      return rootDir;
    }

    @JsonProperty("rootDir")
    public void setRootDir(String rootDir) {
      this.rootDir = rootDir;
    }

    @JsonProperty("userStore")
    public Store getUserStore() {
      return userStore;
    }

    @JsonProperty("userStore")
    public void setUserStore(Store userStore) {
      this.userStore = userStore;
    }

    @JsonProperty("feedStore")
    public Store getFeedStore() {
      return feedStore;
    }

    @JsonProperty("feedStore")
    public void setFeedStore(Store feedStore) {
      this.feedStore = feedStore;
    }
  }

  public static class Store {
    @NotEmpty private String name;
    @NotEmpty private String path;
    @NotEmpty private int maxFileSize;

    @JsonProperty("name")
    public String getName() {
      return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
      this.name = name;
    }

    @JsonProperty("path")
    public String getPath() {
      return path;
    }

    @JsonProperty("path")
    public void setPath(String path) {
      this.path = path;
    }

    @JsonProperty("maxFileSize")
    public int getMaxFileSize() {
      return maxFileSize;
    }

    @JsonProperty("maxFileSize")
    public void setMaxFileSize(int maxFileSize) {
      this.maxFileSize = maxFileSize;
    }
  }
}
