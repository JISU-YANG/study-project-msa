package com.sparta.msa_exam.auth.domain;

import com.sparta.msa_exam.auth.domain.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByUsername(String username);

    Optional<Auth> findByRefreshToken(String refreshToken);
}
