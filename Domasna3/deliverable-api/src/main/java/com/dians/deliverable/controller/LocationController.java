package com.dians.deliverable.controller;

import com.dians.deliverable.models.*;
import com.dians.deliverable.payload.request.AddLocationRequest;
import com.dians.deliverable.payload.response.MessageResponse;
import com.dians.deliverable.service.AddressService;
import com.dians.deliverable.service.CityService;
import com.dians.deliverable.service.JobService;
import com.dians.deliverable.service.StreetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/locations")
public class LocationController {

    private final CityService cityService;
    private final StreetService streetService;
    private final AddressService addressService;
    private final JobService jobService;

    public LocationController(CityService cityService, StreetService streetService, AddressService addressService, JobService jobService) {
        this.cityService = cityService;
        this.streetService = streetService;
        this.addressService = addressService;
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<?> getCities() {
        List<City> cities = cityService.findAll();
        List<String> cityNames = cities.stream().map(City::getName).collect(Collectors.toList());
        return ResponseEntity.ok(cityNames);
    }

    @GetMapping("{cityName}")
    ResponseEntity<?> getStreets(@PathVariable String cityName) {
        cityName = capitalize(cityName);
        City city = cityService.findByName(cityName);
        if(city == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: city not found"));
        }
        List<Street> streets = city.getStreets();
        List<String> streetNames = streets.stream().map(Street::getName).collect(Collectors.toList());
        return ResponseEntity.ok(streetNames);
    }

    @GetMapping("/{cityName}/{streetName}")
    ResponseEntity<?> getAddresses(@PathVariable String cityName, @PathVariable String streetName) {
        cityName = capitalize(cityName);
        String finalStreetName = capitalize(streetName);
        City city = cityService.findByName(cityName);
        if(city == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: city not found"));
        }
        List<Street> streets = city.getStreets();
        Street street = streets.stream()
                .filter(s -> s.getName().equals(finalStreetName))
                .findFirst().orElse(null);

        if(street == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: street not found"));
        }

        List<String> numbersList = street.getAddresses().stream()
                .sorted(Comparator.comparing(Address::getNumber))
                .map(address -> address.getNumber() + "")
                .collect(Collectors.toList());

        return ResponseEntity.ok(numbersList);
    }

    @PostMapping("/addLocation")
    ResponseEntity<?> addLocation(@RequestBody AddLocationRequest addLocationRequest) {

        if(addLocationRequest.getCity().isBlank() || addLocationRequest.getStreet().isBlank()
                || addLocationRequest.getNumber().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Fill out all fields"));
        }

        if(addLocationRequest.getLat() == 0 || addLocationRequest.getLon() == 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Select location on map"));
        }

        int number;
        try {
            number = Integer.parseInt(addLocationRequest.getNumber());
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid number format"));
        }

        String cityName = capitalize(addLocationRequest.getCity());
        String streetName = capitalize(addLocationRequest.getStreet());
        double lat = addLocationRequest.getLat();
        double lon = addLocationRequest.getLon();

        City city = cityService.findByName(cityName);
        if(city == null) {
            Address newAddress = new Address(number, lat, lon);
            addressService.save(newAddress);
            Street newStreet = new Street(streetName);
            newStreet.getAddresses().add(newAddress);
            streetService.save(newStreet);
            City newCity = new City(cityName);
            newCity.getStreets().add(newStreet);
            cityService.save(newCity);
            return ResponseEntity.ok(new MessageResponse("Location added successfully"));
        }

        Street street = city.getStreets().stream()
                .filter(s -> s.getName().equals(streetName))
                .findFirst().orElse(null);

        if(street == null) {
            Street newStreet = new Street(streetName);
            Address newAddress = new Address(number, lat, lon);
            addressService.save(newAddress);
            newStreet.getAddresses().add(newAddress);
            streetService.save(newStreet);
            city.getStreets().add(newStreet);
            cityService.save(city);
            return ResponseEntity.ok(new MessageResponse("Location added successfully"));
        }

        Address address = street.getAddresses().stream()
                .filter(a -> a.getNumber() == number)
                .findFirst().orElse(null);

        if(address == null) {
            Address newAddress = new Address(number, lat, lon);
            addressService.save(newAddress);
            street.getAddresses().add(newAddress);
            streetService.save(street);
            cityService.save(city);
            return ResponseEntity.ok(new MessageResponse("Location added successfully"));
        }

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Location already exists"));
    }

    private String capitalize(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
