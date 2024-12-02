package com.sparta.msa_exam.auth.domain.dto;

import lombok.Getter;

@Getter
public class AuthRequestDto {
    private String username;
    private String password;
}
