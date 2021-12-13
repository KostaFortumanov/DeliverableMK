package com.dians.deliverable.service;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.UserRole;
import com.dians.deliverable.repository.UserRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));

    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public void save(AppUser user) {
        repository.save(user);
    }

    public List<AppUser> getAllDrivers() {
        return repository.findAppUsersByUserRole(UserRole.DRIVER);
    }

    public AppUser getById(Long driverId) {
        return repository.getById(driverId);
    }
}
