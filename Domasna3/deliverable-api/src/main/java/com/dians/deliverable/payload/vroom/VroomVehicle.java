package com.dians.deliverable.payload.vroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VroomVehicle {
    private Long id;
    private double[] start;
    private double[] end;
    private String profile = "driving-car";
    private int[] capacity;
    private int[] time_window;

    public VroomVehicle() {
    }

    public VroomVehicle(Long id, double lon, double lat) {
        this.id = id;
        this.start = new double[]{lon, lat};
        this.end = new double[]{lon, lat};
    }

    public VroomVehicle(Long id, double lon, double lat, int capacity) {
        this.id = id;
        this.start = new double[]{lon, lat};
        this.end = new double[]{lon, lat};
        this.capacity = new int[]{capacity};
    }
}
