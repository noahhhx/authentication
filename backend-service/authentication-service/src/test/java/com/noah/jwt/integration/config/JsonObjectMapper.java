package com.noah.jwt.integration.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonObjectMapper {
  
  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static <T> T asObject(final String json, Class<T> clazz) {
    try {
      return new ObjectMapper().readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
