package com.khoaluan.site.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.khoaluan.common.exception.ProductNotFoundException;
import com.khoaluan.common.model.Product;
import com.khoaluan.site.repository.ProductRepository;

@Service
public class ProductService {

	public static final int PRODUCTS_PER_PAGE = 10;
	
	@Autowired
	ProductRepository productRepository;
	
	public Page<Product> listByCategory(int pageNum, Integer categoryId) {
		String categoryIDMatch = "-" + String.valueOf(categoryId) + "-";
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return productRepository.listByCategory(categoryId, categoryIDMatch, pageable);
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = productRepository.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException("Could not find any Product with alias: " + alias);
		}
		
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNum) {
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		return productRepository.search(keyword, pageable);
	}
	
}
