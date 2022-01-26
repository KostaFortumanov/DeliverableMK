package com.dians.deliverable.job_service.service;

import com.dians.deliverable.job_service.models.City;
import com.dians.deliverable.job_service.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void save(City newCity) {
        repository.save(newCity);
    }
}
