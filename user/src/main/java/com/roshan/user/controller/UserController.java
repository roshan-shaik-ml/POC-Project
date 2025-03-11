package com.roshan.user.controller;

import com.roshan.user.dto.AuthResponse;
import com.roshan.user.dto.LoginRequestDTO;
import com.roshan.user.dto.PreferenceRequestDTO;
import com.roshan.user.dto.UserDTO;
import com.roshan.user.model.Preference;
import com.roshan.user.model.User;
import com.roshan.user.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.roshan.user.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;;

    public UserController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/greet")
    public ResponseEntity<?> greet() {
        return ResponseEntity.ok("Hello, World!");
    }
    @PostMapping("/auth/signup")
    public ResponseEntity<AuthResponse> signUpUser(@RequestBody UserDTO userDTO) {
        AuthResponse response = userService.signUp(userDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponse response = userService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add/preference")
    public ResponseEntity<String> addPreference(@RequestBody PreferenceRequestDTO preferenceRequestDTO) {
        userService.addPreference(preferenceRequestDTO);
        return ResponseEntity.ok("Preference added successfully");
    }
}
