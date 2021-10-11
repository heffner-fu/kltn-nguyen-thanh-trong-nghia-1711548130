package com.khoaluan.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.khoaluan.admin.repository.UserRepository;
import com.khoaluan.common.model.User;

public class ShopmeUserDeatilsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if(user != null) {
			return new ShopmeUserDetails(user);
		}
		throw new UsernameNotFoundException("Could not find any account with email: " + email);
	}

}
