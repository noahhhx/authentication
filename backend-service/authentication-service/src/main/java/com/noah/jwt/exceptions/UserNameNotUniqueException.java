package com.noah.jwt.exceptions;

public class UserNameNotUniqueException extends RuntimeException {

  public UserNameNotUniqueException(String message) {
    super(message);
  }

  public UserNameNotUniqueException(String message, Throwable cause) {
    super(message, cause);
  }
}
