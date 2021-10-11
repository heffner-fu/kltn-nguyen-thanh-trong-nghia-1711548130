package com.khoaluan.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.khoaluan.admin.repository.CountryRepository;
import com.khoaluan.admin.repository.ProductRepository;
import com.khoaluan.admin.repository.ShippingRateRepository;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.exception.ShippingRateAlreadyExistsException;
import com.khoaluan.common.exception.ShippingRateNotFoundException;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.Product;
import com.khoaluan.common.model.ShippingRate;

@Service
@Transactional
public class ShippingRateService {
	private static final int DIM_DIVISOR = 139;	
	@Autowired
	ShippingRateRepository shippingRateRepository;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	ProductRepository productRepository;
	
	public Page<ShippingRate> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, Util.RATES_PER_PAGE, sort);
		if(keyword != null) {
			return shippingRateRepository.findAll(keyword, pageable);
		}
		return shippingRateRepository.findAll(pageable);
	}
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}		
	
	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate rateInDB = shippingRateRepository.findByCountryAndState(
				rateInForm.getCountry().getId(), rateInForm.getState());
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);
		
		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
						+ rateInForm.getCountry().getName() + ", " + rateInForm.getState()); 					
		}
		shippingRateRepository.save(rateInForm);
	}

	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return shippingRateRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
	}
	
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long count = shippingRateRepository.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}		
		shippingRateRepository.updateCODSupport(id, codSupported);
	}
	
	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long count = shippingRateRepository.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);			
		}
		shippingRateRepository.deleteById(id);
	}
	
	public float calculateShippingCost(Integer productId, Integer countryId, String state) 
			throws ShippingRateNotFoundException {
		ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);	
		if (shippingRate == null) {
			throw new ShippingRateNotFoundException("No shipping rate found for the given destination. You have to enter shipping cost manually.");
		}		
		Product product = productRepository.findById(productId).get();		
		float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
		float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;				
		return finalWeight * shippingRate.getRate();
	}
}
