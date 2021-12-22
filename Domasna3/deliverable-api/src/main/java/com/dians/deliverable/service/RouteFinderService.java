package com.dians.deliverable.service;

import com.dians.deliverable.models.Job;
import com.dians.deliverable.payload.response.GetPathResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RouteFinderService {

    @Value("${orsUrl}")
    private String orsUrl;

    private final JobService jobService;

    public RouteFinderService(JobService jobService) {
        this.jobService = jobService;
    }

    public List<List<List<Double>>> getPaths(double startLon, double startLat, List<Job> jobs) {

        List<List<List<Double>>> allPaths = new ArrayList<>();
        if (jobs.size() != 0) {
            allPaths.add(getPath(startLon, startLat, jobs.get(0).getLon(), jobs.get(0).getLat(), jobs.get(0)).getPath());
            for (int i = 0; i < jobs.size() - 1; i++) {
                allPaths.add(getPath(jobs.get(i).getLon(), jobs.get(i).getLat(), jobs.get(i + 1).getLon(), jobs.get(i + 1).getLat(), jobs.get(i + 1)).getPath());
            }
            allPaths.add(getPath(jobs.get(jobs.size() - 1).getLon(), jobs.get(jobs.size() - 1).getLat(), startLon, startLat, null).getPath());

        }

        return allPaths;
    }

    public GetPathResponse getPath(double lon1, double lat1, double lon2, double lat2, Job job) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest r = HttpRequest.newBuilder()
                .uri(URI.create(orsUrl + "/ors/v2/directions/driving-car?start=" + lon1 + "," + lat1 + "&end=" + lon2 + "," + lat2))
                .build();

        HttpResponse response = null;
        try {
            response = client.send(r, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject(response.body().toString());
        JSONArray features = json.getJSONArray("features");
        JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");

        JSONObject properties = features.getJSONObject(0).getJSONObject("properties");
        JSONObject segment = properties.getJSONArray("segments").getJSONObject(0);
        double distance = segment.getDouble("distance");
        double time = segment.getDouble("duration");
        if (job != null) {
            if (job.getDistance() == 0.0) {
                job.setDistance(distance);
                jobService.save(job);
            }

        }

        List<List<Double>> path = new ArrayList<>();
        for (int i = 0; i < coordinates.length(); i++) {
            List<Double> points = new ArrayList<>();
            points.add(coordinates.getJSONArray(i).getDouble(0));
            points.add(coordinates.getJSONArray(i).getDouble(1));
            path.add(points);
        }

        path.add(Arrays.asList(lon2, lat2));

        return new GetPathResponse(distance, time, path);
    }
}
