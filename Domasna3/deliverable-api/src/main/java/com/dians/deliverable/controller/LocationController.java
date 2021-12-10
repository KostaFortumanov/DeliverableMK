package com.dians.deliverable.controller;

import com.dians.deliverable.models.Address;
import com.dians.deliverable.models.City;
import com.dians.deliverable.models.Street;
import com.dians.deliverable.payload.response.MessageResponse;
import com.dians.deliverable.repository.CityRepository;
import com.dians.deliverable.service.CityService;
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

    public LocationController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<?> getCities() {
        List<City> cities = cityService.findAll();
        List<String> cityNames = cities.stream().map(City::getName).collect(Collectors.toList());
        return ResponseEntity.ok(cityNames);
    }

    @GetMapping("{cityName}")
    ResponseEntity<?> getStreets(@PathVariable String cityName) {
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
        City city = cityService.findByName(cityName);
        if(city == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: city not found"));
        }
        List<Street> streets = city.getStreets();
        Street street = streets.stream()
                .filter(s -> s.getName().equals(streetName))
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
}
