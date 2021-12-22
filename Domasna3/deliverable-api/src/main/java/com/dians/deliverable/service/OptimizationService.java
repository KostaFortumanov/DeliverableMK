package com.dians.deliverable.service;

import com.dians.deliverable.exceptions.NoJobsException;
import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Config;
import com.dians.deliverable.models.Job;
import com.dians.deliverable.models.JobStatus;
import com.dians.deliverable.payload.vroom.VroomRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.PortUnreachableException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class OptimizationService {

    @Value("${vroomUrl}")
    private String vroomUrl;

    private final JobService jobService;
    private final UserService userService;
    private final ConfigService configService;

    public OptimizationService(JobService jobService, UserService userService, ConfigService configService) {
        this.jobService = jobService;
        this.userService = userService;
        this.configService = configService;
    }

    public String getVroomResponse(List<Long> driverIds) throws PortUnreachableException, NoJobsException {

        VroomRequest request = buildRequest(driverIds);

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(vroomUrl);
            StringEntity postingString = new StringEntity(new JSONObject(request).toString());
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(post);

            InputStream in = response.getEntity().getContent();
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (in, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            post.releaseConnection();
            return textBuilder.toString();

        } catch (Exception e) {
            throw new PortUnreachableException();
        }
    }

    private VroomRequest buildRequest(List<Long> driverIds) throws NoJobsException {
        VroomRequest vroomRequest = new VroomRequest();

        List<Job> unassignedJobs = jobService.getAllByStatus(JobStatus.NOT_ASSIGNED);

        if(unassignedJobs.size() == 0) {
            throw new NoJobsException();
        }

        unassignedJobs.forEach(job -> vroomRequest.addJob(job.getId(), job.getLon(), job.getLat()));

        Config config = configService.getConfig();
        double startLon = config.getStartLon();
        double startLat = config.getStartLat();
        driverIds.forEach(driverId -> {
            AppUser driver = userService.getById(driverId);
            if(driver.getCurrentJobs().size() == 0) {
                vroomRequest.addDriver(driverId, startLon, startLat);
            }
        });

        int capacity = (int) Math.ceil(1f * unassignedJobs.size() / vroomRequest.getVehicles().size());
        vroomRequest.getVehicles().forEach(vehicle -> {
            vehicle.setCapacity(new int[]{capacity});
            vehicle.setTime_window(new int[]{config.getStartTime().toSecondOfDay()+3600, config.getEndTime().toSecondOfDay()-3600});
        });
        vroomRequest.getJobs().forEach(job -> job.setAmount(new int[]{1}));

        return vroomRequest;
    }
}

