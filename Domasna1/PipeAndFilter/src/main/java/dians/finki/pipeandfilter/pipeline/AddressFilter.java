package dians.finki.pipeandfilter.pipeline;

import dians.finki.pipeandfilter.models.Address;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressFilter implements Filter<String, String>{

    private final String query = "[out:json];" +
            "area(%d)->.searchArea;" +
            "(" +
            "  way[\"building\"](area.searchArea);" +
            "  relation[\"building\"](area.searchArea);" +
            ");" +
            "out center;";

    @Override
    public String process(String input) {
        JSONObject object = new JSONObject(input);
        int osm = object.getInt("osm_id");
        String addresses = getAddresses(osm);

        JSONArray elements = new JSONObject(addresses).getJSONArray("elements");

        HashMap<String, List<Address>> map = new HashMap<>();

        for(int i=0; i<elements.length(); i++) {
            try {

                JSONObject element = elements.getJSONObject(i);

                double lat = element.getJSONObject("center").getDouble("lat");
                double lon = element.getJSONObject("center").getDouble("lon");
                String name = element.getJSONObject("tags").getString("addr:street");
                int number = element.getJSONObject("tags").getInt("addr:housenumber");

                map.computeIfAbsent(name, value -> new ArrayList<>());
                map.get(name).add(new Address(number, lat, lon));

            } catch (JSONException e) {
//                System.out.println("No address");
            }
        }

        JSONObject output = new JSONObject(input);
        JSONArray streets = new JSONArray();

        for(String key: map.keySet()) {
            JSONObject street = new JSONObject();
            street.put("name", key);
            street.put("addresses", map.get(key));
            streets.put(street);
        }

        output.put("streets", streets);
        return output.toString();
    }

    private String getAddresses(int osm) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest r = HttpRequest.newBuilder()
                .uri(URI.create("https://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(String.format(query, 3600000000L + osm ), StandardCharsets.UTF_8)))
                .build();

        HttpResponse response = null;
        try {
            response = client.send(r, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return response.body().toString();
    }
}
