package dians.finki.pipeandfilter.pipeline;

import dians.finki.pipeandfilter.models.Address;
import dians.finki.pipeandfilter.models.City;
import dians.finki.pipeandfilter.models.Street;
import dians.finki.pipeandfilter.repository.AddressRepository;
import dians.finki.pipeandfilter.repository.CityRepository;
import dians.finki.pipeandfilter.repository.StreetRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFilter implements Filter<String, String>{

    private final CityRepository cityRepository;

    private final StreetRepository streetRepository;

    private final AddressRepository addressRepository;

    public DatabaseFilter(CityRepository cityRepository, StreetRepository streetRepository, AddressRepository addressRepository) {
        this.cityRepository = cityRepository;
        this.streetRepository = streetRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public String process(String input) {

        JSONObject object = new JSONObject(input);
        String cityName = object.getString("name");
        JSONArray jsonStreets = object.getJSONArray("streets");
        List<Street> streets = new ArrayList<>();

        for(int i=0; i<jsonStreets.length(); i++) {
            JSONArray jsonAddresses = jsonStreets.getJSONObject(i).getJSONArray("addresses");
            String name = jsonStreets.getJSONObject(i).getString("name");

            List<Address> addresses = new ArrayList<>();
            for(int j=0; j<jsonAddresses.length(); j++) {
                int number = jsonAddresses.getJSONObject(j).getInt("number");
                double lat = jsonAddresses.getJSONObject(j).getDouble("lat");
                double lon = jsonAddresses.getJSONObject(j).getDouble("lon");

                Address address = new Address(number, lat, lon);
                addresses.add(address);
                addressRepository.save(address);
            }

            Street street = new Street(name, addresses);
            streets.add(street);
            streetRepository.save(street);
        }

        City city = new City(cityName, streets);
        cityRepository.save(city);

        return "Kraj";
    }
}

