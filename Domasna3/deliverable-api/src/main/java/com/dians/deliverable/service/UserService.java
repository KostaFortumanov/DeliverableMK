package com.dians.deliverable.service;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public void save(AppUser user) {
        repository.save(user);
    }
}
