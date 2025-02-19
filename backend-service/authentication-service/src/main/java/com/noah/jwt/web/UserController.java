//package com.noah.jwt.web;
//
//import com.noah.jwt.service.UserService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/users")
//@AllArgsConstructor
//public class UserController {
//
//  private final UserService userService;
//
//  @GetMapping("/{id}")
//  public ResponseEntity<UserDto> user(@PathVariable UUID id) {
//    return ResponseEntity.ok(UserDto.from(userService.findById(id)));
//  }
//}
