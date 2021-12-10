package com.dians.deliverable.service;

import com.dians.deliverable.models.City;
import com.dians.deliverable.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

    public List<City> findAll() {
        return repository.findAll();
    }

    public City findByName(String cityName) {
        return repository.findByName(cityName).orElse(null);
    }
}
