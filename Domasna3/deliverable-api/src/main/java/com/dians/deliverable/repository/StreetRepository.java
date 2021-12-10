package com.dians.deliverable.repository;

import com.dians.deliverable.models.Street;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreetRepository extends JpaRepository<Street, Long> {
}
