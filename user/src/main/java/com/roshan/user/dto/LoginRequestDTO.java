package com.roshan.user.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String identifier;  // Can be either email or username
    private String password;
}
