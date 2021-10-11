package com.khoaluan.admin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.khoaluan.admin.exception.BrandNotFoundException;
import com.khoaluan.admin.repository.BrandRepository;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.model.Brand;

@Service
public class BrandService {
	@Autowired
	BrandRepository brandRepository;
	
	public List<Brand> listAll() {
		return (List<Brand>) brandRepository.findAll();
	}
	
	public Page<Brand> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, Util.BRAND_PER_PAGE, sort);
		if(keyword != null) {
			return brandRepository.findAll(keyword, pageable);
		}
		return brandRepository.findAll(pageable);
	}
	
	public Brand save(Brand brand) {
		return brandRepository.save(brand);
	}
	
	public Brand findBrand(Integer id) throws BrandNotFoundException {
		try {
			Optional<Brand> brand = brandRepository.findById(id);
			return brand.get();
		} catch (Exception e) {
			throw new BrandNotFoundException("Brand Id not Found");
		}	
	}
	
	public void delete(Integer id) throws BrandNotFoundException {
		Long countId = brandRepository.countById(id);
		if (countId == null || countId == 0) {
			throw new BrandNotFoundException("Could not find any category with ID: " + id);
		}
		brandRepository.deleteById(id);
	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Brand brandByName = brandRepository.findByName(name);
		if (isCreatingNew) { // Check when create new brand
			if (brandByName != null) {
				return "Duplicate";
			} 
		} else {// Check when update brand
			if (brandByName != null && brandByName.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
	
	
	
}
