package com.example.demo.Controller;

import com.example.demo.Dto.UserDto;
import com.example.demo.Dto.UserUpdateRequest;
import com.example.demo.Entity.User;
import com.example.demo.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @RequestBody UserUpdateRequest request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        UserDto updatedUser = userService.updateProfile(currentUser, request);
        return ResponseEntity.ok(updatedUser);
    }
}
