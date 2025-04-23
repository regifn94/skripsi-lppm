package com.skripsi.lppm.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String userType;

    private List<String> roles;
}
