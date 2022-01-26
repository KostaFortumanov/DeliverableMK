package com.dians.deliverable.job_service.pipeline;

import com.dians.deliverable.job_service.models.Address;
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
                String number = element.getJSONObject("tags").getString("addr:housenumber");

                name = convertCyrilic(name);

                map.computeIfAbsent(name, value -> new ArrayList<>());
                boolean exists = false;
                for(Address address : map.get(name)) {
                    if(address.getNumber().equals(number)) {
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    map.get(name).add(new Address(number, lat, lon));
                }

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

    public static String convertCyrilic(String message){
        char[] abcCyr =   {' ','а','б','в','г','д','ѓ','е', 'ж','з','ѕ','и','ј','к','л','љ','м','н','њ','о','п','р','с','т', 'ќ','у', 'ф','х','ц','ч','џ','ш', 'А','Б','В','Г','Д','Ѓ','Е', 'Ж','З','Ѕ','И','Ј','К','Л','Љ','М','Н','Њ','О','П','Р','С','Т', 'Ќ', 'У','Ф', 'Х','Ц','Ч','Џ','Ш','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6','7','8','9','/','-'};
        String[] abcLat = {" ","a","b","v","g","d","gj","e","zh","z","s","i","j","k","l","lj","m","n","nj","o","p","r","s","t","kj","u","f","h", "c","c", "dz","s","A","B","V","G","D","G","E","Zh","Z","S","I","J","K","L","Lj","M","N","Nj","O","P","R","S","T","Kj","U","F","H", "C","C", "DZ","S", "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","1","2","3","4","5","6","7","8","9","/","-"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++ ) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }

        String str = builder.toString().toLowerCase().trim();
        str = str.substring(0, 1).toUpperCase() + str.substring(1);

        return str;
    }
}
