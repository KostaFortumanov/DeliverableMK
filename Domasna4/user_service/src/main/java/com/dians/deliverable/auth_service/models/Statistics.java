package com.dians.deliverable.auth_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Statistics {

    @Id
    @SequenceGenerator(
            name = "statistics_sequence",
            sequenceName = "statistics_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "statistics_sequence"
    )
    private Long id;

    private double fuel;
    private double distance;
    private LocalDate date;
    private Long appUser;
}
