package com.dians.deliverable.service;

import com.dians.deliverable.models.Config;
import com.dians.deliverable.repository.ConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    private final ConfigRepository repository;

    public ConfigService(ConfigRepository repository) {
        this.repository = repository;
    }

    public Config getConfig() {
        return repository.findAll().get(0);
    }

    public void saveConfig(Config config) {
        repository.save(config);
    }
}
