package com.dians.deliverable.auth_service.repository;

import com.dians.deliverable.auth_service.security.NewAccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewAccountTokenRepository extends JpaRepository<NewAccountToken, Long> {
    Optional<NewAccountToken> findByToken(String token);
}
