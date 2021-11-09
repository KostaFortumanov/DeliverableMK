package dians.finki.pipeandfilter;

import dians.finki.pipeandfilter.pipeline.AddressFilter;
import dians.finki.pipeandfilter.pipeline.DatabaseFilter;
import dians.finki.pipeandfilter.pipeline.OsmFilter;
import dians.finki.pipeandfilter.pipeline.Pipeline;
import dians.finki.pipeandfilter.repository.AddressRepository;
import dians.finki.pipeandfilter.repository.CityRepository;
import dians.finki.pipeandfilter.repository.StreetRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class PipeandfilterApplication {

    private final CityRepository cityRepository;
    private final StreetRepository streetRepository;
    private final AddressRepository addressRepository;

    public PipeandfilterApplication(CityRepository cityRepository, StreetRepository streetRepository, AddressRepository addressRepository) {
        this.cityRepository = cityRepository;
        this.streetRepository = streetRepository;
        this.addressRepository = addressRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PipeandfilterApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomething() {

        Pipeline<String, String> pipeline = new Pipeline<>(new OsmFilter())
                .addFilter(new AddressFilter())
                .addFilter(new DatabaseFilter(cityRepository, streetRepository, addressRepository));

        System.out.println(pipeline.execute("Skopje"));

        System.exit(0);
    }
}
