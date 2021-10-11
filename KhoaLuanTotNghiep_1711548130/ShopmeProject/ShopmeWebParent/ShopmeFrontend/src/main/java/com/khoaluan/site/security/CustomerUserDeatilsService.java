package com.khoaluan.site.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.khoaluan.common.model.Customer;
import com.khoaluan.site.repository.CustomerRepository;

public class CustomerUserDeatilsService implements UserDetailsService {

	@Autowired
	CustomerRepository customerRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = customerRepository.findByEmail(email);
		if(customer != null) {
			return new CustomerUserDetails(customer);
		}
		throw new UsernameNotFoundException("Could not find any customer with email: " + email);
	}

}
