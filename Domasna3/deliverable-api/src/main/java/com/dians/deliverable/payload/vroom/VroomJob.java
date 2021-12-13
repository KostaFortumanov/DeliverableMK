package com.dians.deliverable.payload.vroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
//@Setter
//@AllArgsConstructor
public class VroomJob {

    private Long id;
    private double[] location;
    private final int[] amount = new int[]{1};
    private final int service = 300;

    public VroomJob() {
    }

    public VroomJob(Long id, double lon, double lat) {
        this.id = id;
        this.location = new double[]{lon, lat};
    }
}
