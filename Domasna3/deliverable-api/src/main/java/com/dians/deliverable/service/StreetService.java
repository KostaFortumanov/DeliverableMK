package com.dians.deliverable.service;

import com.dians.deliverable.models.Street;
import com.dians.deliverable.repository.StreetRepository;
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
