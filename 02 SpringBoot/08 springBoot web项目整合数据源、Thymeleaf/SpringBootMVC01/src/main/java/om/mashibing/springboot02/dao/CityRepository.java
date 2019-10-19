package om.mashibing.springboot02.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import om.mashibing.springboot02.entity.City;

public interface CityRepository extends JpaRepository<City, Integer> {

}
