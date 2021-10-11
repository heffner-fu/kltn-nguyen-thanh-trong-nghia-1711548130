package com.khoaluan.admin.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.admin.service.CategoryService;

@RestController
public class CategoryRestController {
	
	@Autowired
	CategoryService categoryService;
	
	@PostMapping("/categories/check_unique")
	public String checkName(Integer id, String name, String alias) {
		return categoryService.checkUnique(id, name, alias);
	}
	
}
