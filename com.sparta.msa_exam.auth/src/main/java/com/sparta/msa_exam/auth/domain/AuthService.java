package com.sparta.msa_exam.auth.domain;

import com.sparta.msa_exam.auth.domain.dto.AuthResponseDto;

public interface AuthService {
    AuthResponseDto login(String username, String password);

    Long register(String username, String password);

    String renewAccessToken(String refreshToken);
}
