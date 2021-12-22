package com.dians.deliverable.controller;

import com.dians.deliverable.models.Config;
import com.dians.deliverable.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
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
