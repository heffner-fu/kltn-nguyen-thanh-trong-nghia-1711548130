package com.khoaluan.admin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.enums.SettingCategory;
import com.khoaluan.common.model.Setting;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {

	public List<Setting> findByCategory(SettingCategory category);
	
}
