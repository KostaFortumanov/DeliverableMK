package com.dians.deliverable.service;

import com.dians.deliverable.models.Address;
import com.dians.deliverable.repository.AddressRepository;
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
