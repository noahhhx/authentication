package com.noah.jwt.web.api;

import com.noah.jwt.api.UsersApiDelegate;
import com.noah.jwt.dto.UserDto;
import com.noah.jwt.mapper.UserMapper;
import com.noah.jwt.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersApiDelegateImpl implements UsersApiDelegate {

  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  public ResponseEntity<UserDto> getUser(String userId) {
    return ResponseEntity.ok(
        userMapper.toUserDto(
            userService.findById(
                UUID.fromString(userId))));
  }
}
