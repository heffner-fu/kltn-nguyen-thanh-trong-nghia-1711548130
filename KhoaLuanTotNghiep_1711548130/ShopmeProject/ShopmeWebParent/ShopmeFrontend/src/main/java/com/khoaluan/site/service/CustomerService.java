package com.khoaluan.site.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.khoaluan.common.enums.AuthenticationType;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.Customer;
import com.khoaluan.site.repository.CountryRepository;
import com.khoaluan.site.repository.CustomerRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	CountryRepository countryRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepository.findByEmail(email);
		return customer == null;
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		customer.setAuthenticationType(AuthenticationType.DATABASE); // right now, authentication type default is DATABASE because FACEBOOK and GOOGLE is not suport
		customerRepository.save(customer);
	}
	
	private void encodePassword(Customer customer) {
		String encode = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encode);
	}
	
	public boolean verify(String verificationCode) {
		Customer customer = customerRepository.findByVerificationCode(verificationCode);
		if (customer == null || customer.isEnabled()) {
			return false;
		} else {
			customerRepository.enabled(customer.getId());
			return true;
		}
	}
	
	public void updateAuthentication(Customer customer, AuthenticationType authenticationType) {
		if (!customer.getAuthenticationType().equals(authenticationType)) {
			customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
		}
	}
	
	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}
	
	public void update(Customer customerInForm) {
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		
		if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!customerInForm.getPassword().isEmpty()) {
				String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(encodedPassword);			
			} else {
				customerInForm.setPassword(customerInDB.getPassword());
			}		
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}
		
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		//customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		
		customerRepository.save(customerInForm);
	}
	
}
