package com.sparta.msa_exam.auth.domain;

import com.sparta.msa_exam.auth.domain.dto.AuthResponseDto;
import com.sparta.msa_exam.auth.domain.model.Auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j(topic = "Auth Service")
@Service
public class AuthServiceImpl implements AuthService {

    public final String BEARER_PREFIX = "Bearer ";
    public final String CLAIM_KEY_USERID = "user_id";
    private final String ISSUER;
    private final Long ACCESS_TOKEN_EXPIRATION_TIME;
    private final Long REFRESH_TOKEN_EXPIRATION_TIME;
    private final AuthRepository authRepository;
    private final SecretKey SECRET_KEY;

    public AuthServiceImpl(
            @Value("${spring.application.name}") String issuer,
            @Value("${service.jwt.secret-key}") String secretKey,
            @Value("${service.jwt.access-expiration}") Long accessExpiration,
            @Value("${service.jwt.refresh-expiration}") Long refreshExpiration,
            AuthRepository authRepository
    ) {
        this.ISSUER = issuer;
        this.SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.ACCESS_TOKEN_EXPIRATION_TIME = accessExpiration;
        this.REFRESH_TOKEN_EXPIRATION_TIME = refreshExpiration;
        this.authRepository = authRepository;
    }

    @Transactional
    @Override
    public AuthResponseDto login(String username, String password) {
        Auth auth = authRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found"));

        auth.updateRefreshToken(generateRefreshToken(auth.getId()));

        return new AuthResponseDto(
                this.generateAccessToken(auth.getId()),
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
                generateAccessToken(auth.getId())
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
        log.info("AuthId: {}", auth.getId());

        refreshToken = refreshToken.substring(BEARER_PREFIX.length());
        Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(refreshToken).getPayload();
        Long userId = Long.valueOf(String.valueOf(claims.get(CLAIM_KEY_USERID)));
        log.info("UserId: {}, All Claims: {}", userId, claims);

        if (!auth.getId().equals(userId)) {
            log.error("User id not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token does not match");
        }

        refreshToken = generateAccessToken(userId);
        log.info("Renewing access token for refreshToken: {}", refreshToken);

        return refreshToken;
    }

    private String generateRefreshToken(Long userId) {
        return BEARER_PREFIX + Jwts.builder()
                .claim(CLAIM_KEY_USERID, userId)
                .issuer(ISSUER)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    private String generateAccessToken(Long userId) {
        return BEARER_PREFIX + Jwts.builder()
                .claim(CLAIM_KEY_USERID, userId)
                .issuer(ISSUER)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

}
