package com.noah.jwt.web.handlers;

import com.noah.dotrecipe.authentication.dto.ErrorDto;
import com.noah.jwt.exceptions.UserNameNotUniqueException;
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
}
