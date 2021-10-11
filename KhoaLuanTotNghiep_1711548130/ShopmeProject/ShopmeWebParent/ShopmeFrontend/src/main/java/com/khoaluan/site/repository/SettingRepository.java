package com.khoaluan.site.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.enums.SettingCategory;
import com.khoaluan.common.model.Setting;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {

	public List<Setting> findByCategory(SettingCategory category);
	
	@Query("SELECT s FROM Setting s WHERE s.category = ?1  OR s.category = ?2")
	public List<Setting> findByTwoCategories(SettingCategory catOne, SettingCategory catTwo);
	
	public Setting findByKey(String key);
	
}
