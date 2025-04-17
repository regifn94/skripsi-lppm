package com.skripsi.lppm.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    public String username;
    public String password;
    public List<String> roles;
}