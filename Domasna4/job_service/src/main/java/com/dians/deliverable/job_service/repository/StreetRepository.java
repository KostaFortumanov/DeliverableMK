package com.dians.deliverable.job_service.repository;

import com.dians.deliverable.job_service.models.Street;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreetRepository extends JpaRepository<Street, Long> {
}
