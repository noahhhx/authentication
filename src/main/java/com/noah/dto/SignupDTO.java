package com.noah.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record SignupDTO(
        String username,
        String password
){}
