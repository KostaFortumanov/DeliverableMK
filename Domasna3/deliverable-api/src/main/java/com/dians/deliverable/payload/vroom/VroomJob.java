package com.dians.deliverable.payload.vroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VroomJob {

    private Long id;
    private double[] location;
    private int[] amount;
    private final int service = 300;
//    private int[][] time_windows = new int[][]{{3600, 6000}, {8000, 10000}};

    public VroomJob() {
    }

    public VroomJob(Long id, double lon, double lat) {
        this.id = id;
        this.location = new double[]{lon, lat};
    }
}
