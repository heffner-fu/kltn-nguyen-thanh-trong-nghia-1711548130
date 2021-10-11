package com.khoaluan.site.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.khoaluan.common.model.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {

	public List<Country> findAllByOrderByNameAsc();
	
}
