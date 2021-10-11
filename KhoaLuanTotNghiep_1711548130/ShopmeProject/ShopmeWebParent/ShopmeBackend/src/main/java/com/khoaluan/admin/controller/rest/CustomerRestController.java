package com.khoaluan.admin.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.admin.service.CustomerService;

@RestController
public class CustomerRestController {

	@Autowired
	CustomerService customerService;
	
	@PostMapping("/customers/check_unique_email")
	public String checkUniqueEmail(Integer id, String email) {
		if (customerService.isEmailUnique(id, email)) {
			return "OK";
		} else {
			return "Duplicated";
		}
	}
	
}
