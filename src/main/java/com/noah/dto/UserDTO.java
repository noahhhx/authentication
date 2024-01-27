package com.noah.dto;

import com.noah.db.document.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class UserDTO {

    private String id;
    private String username;
    private LocalDateTime createdAt;

    public static UserDTO from(User user) {
        return builder()
                .id(user.getId())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
