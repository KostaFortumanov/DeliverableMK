package com.dians.deliverable.navigation_service.controller;

import com.dians.deliverable.navigation_service.service.OptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/optimization")
public class OptimizationController {

    private final OptimizationService optimizationService;

    public OptimizationController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @PostMapping("/vroomResponse")
    public ResponseEntity<String> getVroomResponse(@RequestBody List<Long> driverIds) {
        return ResponseEntity
                .ok(optimizationService.getVroomResponse(driverIds));
    }
}
