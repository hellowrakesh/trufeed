package com.trufeed.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static <T> T fromJsonString(String value, Class<T> clazz) {
    return mapper.convertValue(value, clazz);
  }
}
