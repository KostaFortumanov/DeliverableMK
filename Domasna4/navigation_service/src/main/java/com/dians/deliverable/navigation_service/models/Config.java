package com.dians.deliverable.navigation_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Config {

    @Id
    private Long id;

    private LocalTime startTime;
    private LocalTime endTime;
    private double startLat;
    private double startLon;
}
