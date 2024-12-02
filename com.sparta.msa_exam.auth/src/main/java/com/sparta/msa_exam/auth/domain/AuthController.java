package com.sparta.msa_exam.auth.domain;

import com.sparta.msa_exam.auth.domain.dto.AuthRequestDto;
import com.sparta.msa_exam.auth.domain.dto.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final String HEADER_KEY_ACCESS_TOKEN = "Authorization";
    private final String HEADER_KEY_REFRESH_TOKEN = "X-Refresh-Token";


    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequestDto) {
        AuthResponseDto authResponseDto = authService.login(
                authRequestDto.getUsername(),
                authRequestDto.getPassword()
        );
        HttpHeaders headers = new HttpHeaders();

        headers.set(HEADER_KEY_ACCESS_TOKEN, authResponseDto.getAccessToken());
        headers.set(HEADER_KEY_REFRESH_TOKEN, authResponseDto.getRefreshToken());

        return ResponseEntity.status(200).headers(headers).build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.status(201).body(
                authService.register(authRequestDto.getUsername(), authRequestDto.getPassword())
        );
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("X-Refresh-Token") String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_KEY_ACCESS_TOKEN, authService.renewAccessToken(token));

        return ResponseEntity.status(200).headers(headers).build();
    }
}
