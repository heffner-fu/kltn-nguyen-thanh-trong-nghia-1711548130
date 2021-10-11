package com.khoaluan.admin.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.admin.service.UserService;

@RestController
public class UserRestController {

	@Autowired
	UserService service;
	
	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(Integer id, String email) {	
		return service.isDuplicateEmail(id, email)? "OK":"Duplicated";
	}
	
	@GetMapping("/users/create-data/{number}")
	public String createData(@PathVariable(name = "number")Integer number) {
		try {
			service.createDataTest(number);
			return "OK";
		} catch (Exception e) {
			return "FAILD";
		}
		
	}
	
}
