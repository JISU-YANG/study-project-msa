package com.sparta.msa_exam.auth.domain;

import com.sparta.msa_exam.auth.domain.dto.AuthResponseDto;
import com.sparta.msa_exam.auth.domain.model.Auth;
import com.sparta.msa_exam.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j(topic = "Auth Service")
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final AuthRepository authRepository;

    @Transactional
    @Override
    public AuthResponseDto login(String username, String password) {
        Auth auth = authRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found"));

        auth.updateRefreshToken(jwtUtil.generateRefreshToken(auth.getId()));

        return new AuthResponseDto(
                jwtUtil.generateAccessToken(auth.getId()),
                auth.getRefreshToken()
        );
    }

    @Transactional
    @Override
    public Long register(String username, String password) {
        if (authRepository.findByUsername(username).isPresent()) {
            log.error("Username is already taken");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        Auth auth = Auth.builder()
                .username(username)
                .password(password)
                .refreshToken(null)
                .build();

        authRepository.save(auth);
        log.info("Registering user {}", username);

        auth.updateRefreshToken(
                jwtUtil.generateAccessToken(auth.getId())
        );

        log.info("Registered userId: {}, refreshToken: {}", auth.getId(), auth.getRefreshToken());
        return auth.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public String renewAccessToken(String refreshToken) {
        Auth auth = authRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));
        log.info("Renewing access token for refreshToken: {}", refreshToken);
        log.info("AuthId from Database: {}", auth.getId());

        Long authId = Long.valueOf(jwtUtil.getClaimValueFromToken(refreshToken, jwtUtil.CLAIM_KEY_USERID));
        log.info("AuthId from Token: {}", authId);

        if (!auth.getId().equals(authId)) {
            log.error("User id not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token does not match");
        }

        refreshToken = jwtUtil.generateAccessToken(authId);
        log.info("Renewing access token for refreshToken: {}", refreshToken);

        return refreshToken;
    }
}
