package om.mashibing.springboot02.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import om.mashibing.springboot02.domain.City;

@Repository
public class CityDAO {
	
	/**
	 *     在内存中虚拟出一份数据
	 *     List
	 *     Map
	 * 需要保证线程安全
	 *     
	 * @return
	 */
	
	static Map<Integer, City> dataMap =  Collections.synchronizedMap(new HashMap<Integer, City>());
	
	public List<City> findAll() {
		
		return new ArrayList<>(dataMap.values());
	}

	public void save(City city)throws Exception {

		City data = dataMap.get(city.getId());
		if(data != null) {
			throw new Exception("数据已存在");
		}else {
			
			dataMap.put(city.getId(), city);
			System.out.println("数据已添加");
		}
		
	}
	
	

}
