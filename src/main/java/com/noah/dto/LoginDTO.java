package com.noah.dto;

import lombok.Builder;

@Builder
public record LoginDTO(
        String username, String password
){}
