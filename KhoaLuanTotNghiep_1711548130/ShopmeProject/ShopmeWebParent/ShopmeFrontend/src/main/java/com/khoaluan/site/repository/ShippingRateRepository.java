package com.khoaluan.site.repository;

import org.springframework.data.repository.CrudRepository;

import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
	
	public ShippingRate findByCountryAndState(Country country, String state);
	
}
