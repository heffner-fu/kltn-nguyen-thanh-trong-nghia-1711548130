package com.khoaluan.site.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khoaluan.common.model.Address;
import com.khoaluan.common.model.Customer;
import com.khoaluan.common.model.ShippingRate;
import com.khoaluan.site.repository.ShippingRateRepository;

@Service
public class ShippingRateService {

	@Autowired 
	ShippingRateRepository shippingRateRepository;
	
	public ShippingRate getShippingRateForCustomer(Customer customer) {
		String state = customer.getState();
		if (state == null || state.isEmpty()) {
			state = customer.getCity();
		}		
		return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
	}
	
	public ShippingRate getShippingRateForAddress(Address address) {
		String state = address.getState();
		if (state == null || state.isEmpty()) {
			state = address.getCity();
		}	
		return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
	}
	
}
