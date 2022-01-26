package com.dians.deliverable.navigation_service.payload.vroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VroomVehicle {
    private Long id;
    private double[] start;
    private double[] end;
    private String profile = "driving-car";
    private int[] capacity;
    private int[] time_window;

    public VroomVehicle(Long id, double lon, double lat) {
        this.id = id;
        this.start = new double[]{lon, lat};
        this.end = new double[]{lon, lat};
    }
}
