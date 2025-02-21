package com.noah.jwt.mapper;

import com.noah.jwt.dto.RegisterDto;
import com.noah.jwt.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
    componentModel = "spring",
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {

  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  User toUser(RegisterDto registerDto);
}
