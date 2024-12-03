package com.sparta.msa_exam.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    public final static String HEADER_KEY_ACCESS_TOKEN = "Authorization";
    public final static String HEADER_KEY_REFRESH_TOKEN = "X-Refresh-Token";
    public final String BEARER_PREFIX = "Bearer ";
    public final String CLAIM_KEY_USERID = "user_id";
    private final String ISSUER = "auth";
    private final Long ACCESS_TOKEN_EXPIRATION_TIME;
    private final Long REFRESH_TOKEN_EXPIRATION_TIME;
    private final SecretKey SECRET_KEY;

    public JwtUtil(
            @Value("${service.jwt.secret-key}") String secretKey,
            @Value("${service.jwt.access-expiration}") Long accessExpiration,
            @Value("${service.jwt.refresh-expiration}") Long refreshExpiration
    ) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.ACCESS_TOKEN_EXPIRATION_TIME = accessExpiration;
        this.REFRESH_TOKEN_EXPIRATION_TIME = refreshExpiration;
    }

    public String getClaimValueFromToken(String token, String key) {
        token = token.substring(BEARER_PREFIX.length());
        return String.valueOf(
                Jwts.parser()
                        .verifyWith(SECRET_KEY)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload().get(key)
        );
    }

    public HttpHeaders setHeaderTokens(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_KEY_ACCESS_TOKEN, accessToken);
        return headers;
    }

    public HttpHeaders setHeaderTokens(String accessToken, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_KEY_ACCESS_TOKEN, accessToken);
        headers.add(HEADER_KEY_REFRESH_TOKEN, refreshToken);
        return headers;
    }

    public boolean validateToken(String token) {
        try {
            token = token.substring(BEARER_PREFIX.length());

            Claims payload = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            log.debug("JWT claims string: {}", payload);

            String issuer = payload.getIssuer();
            if (issuer == null || !issuer.equals(ISSUER)) {
                log.error("Invalid issuer");
                return false;
            }
            log.debug("Validated token issuer");

            Long userId = payload.get(CLAIM_KEY_USERID, Long.class);
            if(userId == null || userId <= 0) {
                log.error("Invalid userId");
                return false;
            }
            log.debug("Validated token userId");

            Date expiration = payload.getExpiration();
            if(expiration == null || expiration.before(new Date())) {
                log.error("Invalid expiration");
                return false;
            }
            log.debug("Validated token expiration");

            return true;
        }catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException, Token has expired: {}" , e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("MalformedJwtException, Malformed token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception, Invalid token: {}", e.getMessage());
        }

        return false;
    }

    public String generateRefreshToken(Long userId) {
        return BEARER_PREFIX + Jwts.builder()
                .claim(CLAIM_KEY_USERID, userId)
                .issuer(ISSUER)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String generateAccessToken(Long userId) {
        return BEARER_PREFIX + Jwts.builder()
                .claim(CLAIM_KEY_USERID, userId)
                .issuer(ISSUER)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}
