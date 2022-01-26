package com.dians.deliverable.navigation_service.repository;

import com.dians.deliverable.navigation_service.models.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
}
