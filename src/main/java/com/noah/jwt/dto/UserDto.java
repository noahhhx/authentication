package com.noah.jwt.dto;

import com.noah.jwt.db.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class UserDto {

  private UUID id;
  private String username;
  private LocalDateTime createdAt;

  public static UserDto from(User user) {
    return builder()
            .id(user.getId())
            .username(user.getUsername())
            .createdAt(user.getCreatedAt())
            .build();
  }
}
