package com.khoaluan.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.khoaluan.admin.repository.CountryRepository;
import com.khoaluan.admin.repository.CustomerRepository;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.exception.CustomerNotFoundException;
import com.khoaluan.common.exception.ProductNotFoundException;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.Customer;

@Service
@Transactional
public class CustomerService {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CountryRepository countryRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public Page<Customer> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, Util.CUSTOMERS_PER_PAGE, sort);
		if(keyword != null) {
			return customerRepository.findAll(keyword, pageable);
		}
		return customerRepository.findAll(pageable);
	}
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public List<Customer> getListAll() {
		return (List<Customer>) customerRepository.findAll();
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null && customer.getId() != id) {
			return false;
		}		
		return true;
	}
	
	public void save(Customer customerInForm) {		
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		if (!customerInForm.getPassword().isEmpty()) {
			String encodePassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodePassword);
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		//customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		//customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		customerRepository.save(customerInForm);
	}
	
	public String checkUnique(Integer id, String email) {
		boolean isCreatingNew = (id == null || id == 0);
		Customer customer = customerRepository.findByEmail(email);
		if (isCreatingNew) { // Check when create new brand
			if (customer != null) {
				return "Duplicate";
			} 
		} else {// Check when update brand
			if (customer != null && customer.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
	
	public void updateEnabledStatus(Integer id, boolean enabled) {
		customerRepository.updateEnabledStatus(id, enabled);
		Customer customer = customerRepository.findById(id).get();
		customer.setVerificationCode(null);
		customerRepository.save(customer);
	}
	
	public void delete(Integer id) throws ProductNotFoundException {
		Long countId = customerRepository.countById(id);
		if (countId == null || countId == 0) {
			throw new ProductNotFoundException("Could not find any Customer with ID: " + id);
		}
		customerRepository.deleteById(id);
	}
	
	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not found any Customer with ID " + id);
		}
	}
	
}
