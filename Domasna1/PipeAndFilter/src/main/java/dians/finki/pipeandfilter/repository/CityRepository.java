package dians.finki.pipeandfilter.repository;

import dians.finki.pipeandfilter.models.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
