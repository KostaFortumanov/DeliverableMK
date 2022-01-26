package com.dians.deliverable.navigation_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    private Long id;

    private double fuel;
    private double distance;
    private LocalDate date;
    private Long appUser;

    public Statistics(double fuel, double distance, LocalDate date, Long appUser) {
        this.fuel = fuel;
        this.distance = distance;
        this.date = date;
        this.appUser = appUser;
    }
}
