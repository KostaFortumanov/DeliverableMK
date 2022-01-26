package com.dians.deliverable.navigation_service.payload.vroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VroomRequest {

    private List<VroomJob> jobs = new ArrayList<>();
    private List<VroomVehicle> vehicles = new ArrayList<>();

    public void addJob(Long id, double lon, double lat) {
        jobs.add(new VroomJob(id ,lon, lat));
    }

    public void addDriver(Long id, double lon, double lat) {
        vehicles.add(new VroomVehicle(id, lon, lat));
    }
}