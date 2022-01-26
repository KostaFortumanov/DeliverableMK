package com.dians.deliverable.navigation_service.payload.vroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VroomJob {

    private Long id;
    private double[] location;
    private int[] amount;
    private final int service = 300;

    public VroomJob(Long id, double lon, double lat) {
        this.id = id;
        this.location = new double[]{lon, lat};
    }
}
