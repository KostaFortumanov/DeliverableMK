package com.dians.deliverable.service;

import com.dians.deliverable.repository.NewAccountTokenRepository;
import com.dians.deliverable.security.NewAccountToken;
import org.springframework.stereotype.Service;

@Service
public class NewAccountTokenService {

    private final NewAccountTokenRepository repository;

    public NewAccountTokenService(NewAccountTokenRepository repository) {
        this.repository = repository;
    }

    public void save(NewAccountToken token) {
        repository.save(token);
    }

    public NewAccountToken getToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    public void delete(NewAccountToken token) {
        repository.delete(token);
    }
}
