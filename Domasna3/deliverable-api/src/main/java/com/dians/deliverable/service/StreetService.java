package com.dians.deliverable.service;

import com.dians.deliverable.repository.StreetRepository;
import org.springframework.stereotype.Service;

@Service
public class StreetService {

    private final StreetRepository repository;

    public StreetService(StreetRepository repository) {
        this.repository = repository;
    }
}
