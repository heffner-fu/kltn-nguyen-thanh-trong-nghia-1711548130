package com.khoaluan.admin.service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.khoaluan.admin.repository.ProductRepository;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.exception.ProductNotFoundException;
import com.khoaluan.common.model.Product;

@Service
@Transactional
public class ProductService {
	@Autowired
	ProductRepository productRepository;
	
	public List<Product> getListAll() {
		return (List<Product>) productRepository.findAll();
	}
	
	public Page<Product> listByPage(int pageNumber, String sortField, String sortDir, String keyword, Integer categoryId) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, Util.PRODUCT_PER_PAGE, sort);
		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0) { // Using when select category and use keyword
				String categoryIdMatch = "-" + categoryId + "-";
				return productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			}
			
			return productRepository.findAll(keyword, pageable);// Not use select category but use keyword
		}
		if (categoryId != null && categoryId > 0) { // Using when select category and not use keyword
			String categoryIdMatch = "-" + categoryId + "-";
			return productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
		}
		return productRepository.findAll(pageable);
	}
	
	public Page<Product> searchProducts(int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, Util.PRODUCT_PER_PAGE, sort);
		if (keyword == null) {
			return productRepository.findAll(pageable);
		}
		return productRepository.searchProductsByName(keyword, pageable);
	}
	
	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replace(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replace(" ", "-"));
		}
		product.setUpdatedTime(new Date());
		return productRepository.save(product);
	}
	
	public void saveProductPrice(Product productInForm) {
		Product product = productRepository.findById(productInForm.getId()).get();
		product.setPrice(productInForm.getPrice());
		product.setCost(productInForm.getCost());
		product.setDiscountPercent(productInForm.getDiscountPercent());
		productRepository.save(product);
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product product = productRepository.findByName(name);
		if (isCreatingNew) { // Check when create new brand
			if (product != null) {
				return "Duplicate";
			} 
		} else {// Check when update brand
			if (product != null && product.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
	
	public void updateEnabledStatus(Integer id, boolean enabled) {
		productRepository.updateEnabledStatus(id, enabled);
	}
	
	public void delete(Integer id) throws ProductNotFoundException {
		Long countId = productRepository.countById(id);
		if (countId == null || countId == 0) {
			throw new ProductNotFoundException("Could not find any product with ID: " + id);
		}
		productRepository.deleteById(id);
	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return productRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not found any product with ID " + id);
		}
	}
}
