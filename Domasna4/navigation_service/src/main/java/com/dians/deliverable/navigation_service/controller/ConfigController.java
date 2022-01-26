package com.dians.deliverable.navigation_service.controller;

import com.dians.deliverable.navigation_service.models.Config;
import com.dians.deliverable.navigation_service.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<?> getConfig() {
        return ResponseEntity
                .ok(configService.getConfig());
    }

    @PostMapping
    public void saveConfig(@RequestBody Config config) {
        config.setId(1L);
        configService.saveConfig(config);
    }
}
