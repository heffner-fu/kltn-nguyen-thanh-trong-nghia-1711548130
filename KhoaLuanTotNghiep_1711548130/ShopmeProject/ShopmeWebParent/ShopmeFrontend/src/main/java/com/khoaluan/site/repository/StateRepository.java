package com.khoaluan.site.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.State;

public interface StateRepository extends CrudRepository<State, Integer> {

	public List<State> findByCountryOrderByNameAsc(Country country);
	
}
