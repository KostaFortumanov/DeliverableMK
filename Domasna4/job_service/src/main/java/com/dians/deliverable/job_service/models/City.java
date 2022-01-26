package com.dians.deliverable.job_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @OneToMany
    private List<Street> streets;

    public City(String name) {
        this.name = name;
        streets = new ArrayList<>();
    }

    public City(String name, List<Street> streets) {
        this.name = name;
        this.streets = streets;
    }
}
