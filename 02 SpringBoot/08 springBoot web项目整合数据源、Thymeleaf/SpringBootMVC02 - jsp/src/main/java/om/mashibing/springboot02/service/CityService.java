package om.mashibing.springboot02.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import om.mashibing.springboot02.dao.CityRepository;
import om.mashibing.springboot02.entity.City;

@Service
public class CityService {

	@Autowired
	// Repository == Dao
	CityRepository cityRepo;
	
	public List<City> findAll() {

		List<City> findAll = cityRepo.findAll();
		return findAll;
	}

	public City findOne(Integer id) {
		
		return cityRepo.getOne(id);
	}

}
