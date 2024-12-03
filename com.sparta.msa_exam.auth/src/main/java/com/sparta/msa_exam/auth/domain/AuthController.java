package com.sparta.msa_exam.auth.domain;

import com.sparta.msa_exam.auth.domain.dto.AuthRequestDto;
import com.sparta.msa_exam.auth.domain.dto.AuthResponseDto;
import com.sparta.msa_exam.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequestDto) {
        AuthResponseDto authResponseDto = authService.login(
                authRequestDto.getUsername(),
                authRequestDto.getPassword()
        );

        return ResponseEntity.status(200).headers(
                jwtUtil.setHeaderTokens(
                        authResponseDto.getAccessToken(),
                        authResponseDto.getRefreshToken()
                )
        ).build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.status(201).body(
                authService.register(authRequestDto.getUsername(), authRequestDto.getPassword())
        );
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(name=JwtUtil.HEADER_KEY_REFRESH_TOKEN) String token) {
        return ResponseEntity.status(200).headers(
                jwtUtil.setHeaderTokens(authService.renewAccessToken(token))
        ).build();
    }
}
