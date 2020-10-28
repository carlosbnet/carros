package com.example.carros.api.controller.security.jwt;


import lombok.Data;

@Data
public class JwtLoginInput {
    private String username;
    private String password;
}
