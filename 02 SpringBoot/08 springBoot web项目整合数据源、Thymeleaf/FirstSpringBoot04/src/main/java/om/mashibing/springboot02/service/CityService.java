package om.mashibing.springboot02.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import om.mashibing.springboot02.dao.CityDAO;
import om.mashibing.springboot02.domain.City;

@Service
public class CityService {

	@Autowired
	CityDAO cityDao;
	
	
	public List<City> findAll() {

		return cityDao.findAll();
	}


	public String add(Integer id, String name) {

		City city = new City();
		city.setId(id);
		city.setName(name);
		
		try {
			cityDao.save(city);
			return "保存成功";
		} catch (Exception e) {
			return "保失败";
		}
		
	}


	public String add(City city) {
		
		try {
			cityDao.save(city);
			return "保存成功";
		} catch (Exception e) {
			return "保失败";
		}
				
	}

}
