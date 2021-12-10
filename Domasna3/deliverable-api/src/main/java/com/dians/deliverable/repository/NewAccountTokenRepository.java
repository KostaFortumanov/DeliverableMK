package com.dians.deliverable.repository;

import com.dians.deliverable.security.NewAccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewAccountTokenRepository extends JpaRepository<NewAccountToken, Long> {
    Optional<NewAccountToken> findByToken(String token);
}
