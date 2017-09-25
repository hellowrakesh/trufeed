package com.trufeed.service;

import java.util.UUID;

public abstract class Service {

  protected String generateUuid(String key) {
    return UUID.nameUUIDFromBytes(key.getBytes()).toString();
  }
}
