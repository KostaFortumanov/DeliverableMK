package com.dians.deliverable.job_service.service;

import com.dians.deliverable.job_service.models.Address;
import com.dians.deliverable.job_service.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    public void save(Address newAddress) {
        repository.save(newAddress);
    }
}
