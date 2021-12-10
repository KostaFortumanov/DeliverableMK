package com.dians.deliverable.service;

import com.dians.deliverable.repository.CityRepository;
import org.springframework.stereotype.Service;

@Service
public class CityService {

    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }
}
