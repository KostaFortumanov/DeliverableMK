package com.dians.deliverable.job_service;

import com.dians.deliverable.job_service.pipeline.AddressFilter;
import com.dians.deliverable.job_service.pipeline.DatabaseFilter;
import com.dians.deliverable.job_service.pipeline.OsmFilter;
import com.dians.deliverable.job_service.pipeline.Pipeline;
import com.dians.deliverable.job_service.repository.AddressRepository;
import com.dians.deliverable.job_service.repository.CityRepository;
import com.dians.deliverable.job_service.repository.StreetRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class JobServiceApplication {

    private final CityRepository cityRepository;
    private final StreetRepository streetRepository;
    private final AddressRepository addressRepository;

    public JobServiceApplication(CityRepository cityRepository, StreetRepository streetRepository, AddressRepository addressRepository) {
        this.cityRepository = cityRepository;
        this.streetRepository = streetRepository;
        this.addressRepository = addressRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(JobServiceApplication.class, args);
    }

    @PostConstruct
    public void addAddresses() {

        if(!cityRepository.findByName("Skopje").isPresent()) {
            Pipeline<String, String> pipeline = new Pipeline<>(new OsmFilter())
                    .addFilter(new AddressFilter())
                    .addFilter(new DatabaseFilter(cityRepository, streetRepository, addressRepository));

            System.out.println(pipeline.execute("Skopje"));
        }
    }

}
