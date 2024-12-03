package com.sparta.msa_exam.gateway.filter;

import com.sparta.msa_exam.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "JwtAuthenticationFilter")
@RequiredArgsConstructor
@Component
public class LocalJwtAuthenticationFilter implements GlobalFilter {
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(JwtUtil.HEADER_KEY_ACCESS_TOKEN);
        String uri = exchange.getRequest().getURI().getPath();

        if (uri.startsWith("/auth")) {
            log.info("Pass the JWT Token Validate, URI: {}", uri);
            return chain.filter(exchange);
        }

        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            log.error("JWT Token Validate Fail, Token: {}", token);
            return exchange.getResponse().setComplete();
        }

        log.info("JWT Token Validate Success");
        return chain.filter(exchange);
    }

}
