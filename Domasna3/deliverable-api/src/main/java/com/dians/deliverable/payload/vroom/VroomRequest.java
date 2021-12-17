package com.dians.deliverable.payload.vroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class VroomRequest {

    private List<VroomJob> jobs = new ArrayList<>();
    private List<VroomVehicle> vehicles = new ArrayList<>();

    public VroomRequest() {
    }

    public void addJob(Long id, double lon, double lat) {
        jobs.add(new VroomJob(id ,lon, lat));
    }

    public void addDriver(Long id, double lon, double lat) {
        vehicles.add(new VroomVehicle(id, lon, lat));
    }

    public void addDriver(Long id, double lon, double lat, int capacity) {
        vehicles.add(new VroomVehicle(id, lon, lat, capacity));
    }
}

