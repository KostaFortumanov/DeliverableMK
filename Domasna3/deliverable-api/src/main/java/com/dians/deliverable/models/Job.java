package com.dians.deliverable.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table(name = "job")
public class Job {

    @SequenceGenerator(
            name = "job_sequence",
            sequenceName = "job_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "job_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;
    private String address;
    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;
    private double lat;
    private double lon;
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    @OneToOne
    private AppUser assignedTo;
    private Double distance = 0.0;

    public Job() {
    }

    public Job(String address, String description, double lat, double lon, JobStatus status) {
        this.address = address;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
    }
}
