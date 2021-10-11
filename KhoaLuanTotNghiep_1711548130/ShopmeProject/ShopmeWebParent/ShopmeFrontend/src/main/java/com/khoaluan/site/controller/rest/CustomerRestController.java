package com.khoaluan.site.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.site.service.CustomerService;

@RestController
public class CustomerRestController {

	@Autowired
	CustomerService customerService;
	
	@PostMapping("/customers/check_unique_email")
	public String checkUniqueEmail(@Param("email") String email) {
		return customerService.isEmailUnique(email) ? "OK" : "Duplicated";
	}
}
