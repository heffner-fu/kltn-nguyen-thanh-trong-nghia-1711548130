package com.khoaluan.admin.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.admin.DTO.ProductDTO;
import com.khoaluan.admin.service.ProductService;
import com.khoaluan.common.exception.ProductNotFoundException;
import com.khoaluan.common.model.Product;

@RestController
public class ProductRestController {

	@Autowired
	ProductService productService;
	
	@PostMapping("/products/check_unique")
	public String checkUnique(Integer id, String name) {
		return productService.checkUnique(id, name);
	}
	
	@GetMapping("/products/get/{id}")
	public ProductDTO getProductInfo(@PathVariable("id") Integer id) 
			throws ProductNotFoundException {
		Product product = productService.get(id);
		return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
	}
	
}
