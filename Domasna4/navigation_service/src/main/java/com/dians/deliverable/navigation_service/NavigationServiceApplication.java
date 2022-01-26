package com.dians.deliverable.navigation_service;

import com.dians.deliverable.navigation_service.models.Config;
import com.dians.deliverable.navigation_service.service.ConfigService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.LocalTime;

@SpringBootApplication
public class NavigationServiceApplication {

    private final ConfigService configService;

    public NavigationServiceApplication(ConfigService configService) {
        this.configService = configService;
    }

    public static void main(String[] args) {
        SpringApplication.run(NavigationServiceApplication.class, args);
    }

    @PostConstruct
    public void addConfig() {
        if(!configService.configExists()) {
            configService.saveConfig(
                    new Config(
                            1L,
                            LocalTime.of(8, 0, 0),
                            LocalTime.of(17, 0, 0),
                            41.989425837223365,
                            21.43295288085938));
        }
    }

}
