package com.noah.web;

import com.noah.dto.UserDTO;
import com.noah.service.UserManager;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserManager userManager;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> user(@PathVariable UUID id) {
        return ResponseEntity.ok(UserDTO.from(userManager.findById(id)));
    }
}
