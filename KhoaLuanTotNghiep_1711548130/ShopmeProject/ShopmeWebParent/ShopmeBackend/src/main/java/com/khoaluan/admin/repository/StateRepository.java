package com.khoaluan.admin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.State;

@Repository
public interface StateRepository extends CrudRepository<State, Integer> {

	public List<State> findByCountryOrderByNameAsc(Country country);
	
}
