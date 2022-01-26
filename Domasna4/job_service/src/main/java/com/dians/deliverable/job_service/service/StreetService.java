package com.dians.deliverable.job_service.service;

import com.dians.deliverable.job_service.models.Street;
import com.dians.deliverable.job_service.repository.StreetRepository;
import org.springframework.stereotype.Service;

@Service
public class StreetService {

    private final StreetRepository repository;

    public StreetService(StreetRepository repository) {
        this.repository = repository;
    }

    public void save(Street newStreet) {
        repository.save(newStreet);
    }

    public Street findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
