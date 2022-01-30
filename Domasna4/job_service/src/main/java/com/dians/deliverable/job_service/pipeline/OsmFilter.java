package com.dians.deliverable.job_service.pipeline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class OsmFilter implements Filter<String, String> {

    @Override
    public String process(String input) {

        int osm = 6966465;
        JSONObject output = new JSONObject();
        output.put("name", input);
        output.put("osm_id", osm);

        return output.toString();
    }

    private int getOsm(String cityName) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest r = HttpRequest.newBuilder()
                .uri(URI.create("https://nominatim.openstreetmap.org/search?format=json&q=" + URLEncoder.encode(cityName + ", Macedonia", StandardCharsets.UTF_8)))
                .build();

        HttpResponse response = null;
        try {
            response = client.send(r, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        String body = response.body().toString();
        System.out.println(body);
        JSONArray arr = new JSONArray(body);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject object = arr.getJSONObject(i);
            String type = object.getString("type");
            if (type.equals("administrative")) {
                return object.getInt("osm_id");
            }
        }

        return 0;
    }
}
