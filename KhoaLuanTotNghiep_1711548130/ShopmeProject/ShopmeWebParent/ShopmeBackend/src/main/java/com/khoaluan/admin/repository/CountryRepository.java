package com.khoaluan.admin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.model.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country, Integer> {

	public List<Country> findAllByOrderByNameAsc();
	
}
