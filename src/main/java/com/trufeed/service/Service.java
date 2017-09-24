package com.trufeed.service;

import java.util.UUID;

public abstract class Service {

  protected String generateNewUuid(String key) {
    return UUID.fromString(key).toString();
  }
}
