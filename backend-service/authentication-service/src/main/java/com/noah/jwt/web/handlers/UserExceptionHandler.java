package com.noah.jwt.web.handlers;

import com.noah.jwt.dto.ErrorDto;
import com.noah.jwt.exceptions.UserLockedException;
import com.noah.jwt.exceptions.UserNameNotUniqueException;
import com.noah.jwt.exceptions.UserNotExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

  @ExceptionHandler(UserNameNotUniqueException.class)
  public ResponseEntity<ErrorDto> handleUserNameNotUniqueException(UserNameNotUniqueException ex) {
    log.error("User name not unique error occurred: {}", ex.getMessage(), ex);
    ErrorDto error = new ErrorDto(
        "DUPLICATE_USER_NAME",
        "A user with the same name already exists"
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }
  
  @ExceptionHandler(UserNotExistsException.class)
  public ResponseEntity<ErrorDto> handleUserNotExistsException(UserNotExistsException ex) {
    log.error("User not exists error occurred: {}", ex.getMessage(), ex);
    ErrorDto error = new ErrorDto(
        "USER_NOT_EXISTS",
        "User does not exist"
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
  
  @ExceptionHandler(UserLockedException.class)
  public ResponseEntity<ErrorDto> handleUserLockedException(UserLockedException ex) {
    log.error("User locked error occurred: {}", ex.getMessage(), ex);
    ErrorDto error = new ErrorDto(
        "USER_LOCKED",
        "User is locked"
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }
}
