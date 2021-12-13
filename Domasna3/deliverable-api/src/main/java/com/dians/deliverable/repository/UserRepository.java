package com.dians.deliverable.repository;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
    List<AppUser> findAppUsersByUserRole(UserRole userRole);
}
