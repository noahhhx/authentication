package com.noah.jwt.exceptions;

public class UserLockedException extends RuntimeException {
  public UserLockedException(String message) {
    super(message);
  }
}
