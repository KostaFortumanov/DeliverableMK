package com.dians.deliverable.auth_service.service;

import com.dians.deliverable.auth_service.models.AppUser;
import com.dians.deliverable.auth_service.models.UserRole;
import com.dians.deliverable.auth_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return repository.findById(driverId).orElse(null);
    }

    public void deleteUser(AppUser user) {
        repository.delete(user);
    }
}
