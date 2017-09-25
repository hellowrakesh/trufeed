package com.trufeed.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {

  protected static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

  private static final ThreadLocal<DateFormat> DATE_FORMAT_LOCAL = new ThreadLocal<>();

  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper().setDateFormat(getDateFormat());
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
  }

  public static DateFormat getDateFormat() {
    DateFormat dateFormat = DATE_FORMAT_LOCAL.get();
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      DATE_FORMAT_LOCAL.set(dateFormat);
    }
    return dateFormat;
  }

  public static Date stringToDate(String dateValue) {
    try {
      return getDateFormat().parse(dateValue);
    } catch (ParseException parseException) {
      LOG.error("Error while parsing date string: " + dateValue, parseException);
      return null;
    }
  }

  public static String dateToString(Date date) {
    return getDateFormat().format(date);
  }

  public static <T> String fromObjectToJsonString(T object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException exception) {
      LOG.error("Unable to convert object: " + object + " to Json string", exception);
      throw new RuntimeException(exception);
    }
  }

  public static <T> T fromJsonStringToObject(String value, TypeReference<T> ref) {
    try {
      return mapper.readValue(value, ref);
    } catch (Exception exception) {
      LOG.error(
          "Unable to convert json string: " + value + " to object of type: " + ref, exception);
      throw new RuntimeException(exception);
    }
  }

  public static <T> T fromJsonStringToObject(String value, Class<T> clazz) {
    try {
      return mapper.readValue(value, clazz);
    } catch (Exception exception) {
      LOG.error(
          "Unable to convert json string: " + value + " to object of type: " + clazz, exception);
      throw new RuntimeException(exception);
    }
  }
}
