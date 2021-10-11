package com.khoaluan.admin.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.admin.DTO.CategoryDTO;
import com.khoaluan.admin.exception.BrandNotFoundException;
import com.khoaluan.admin.exception.BrandNotFoundRestException;
import com.khoaluan.admin.service.BrandService;
import com.khoaluan.common.model.Brand;
import com.khoaluan.common.model.Category;

@RestController
public class BrandRestController {

	@Autowired
	BrandService brandService;
	
	@PostMapping("/brands/check_unique")
	public String checkName(Integer id, String name) {
		return brandService.checkUnique(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
		try {
			List<CategoryDTO> list = new ArrayList<>();
			Brand brand = brandService.findBrand(brandId);
			Set<Category> categories = brand.getCategories();
			for(Category category : categories) {
				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
				list.add(dto);
			}
			return list;
		} catch (BrandNotFoundException e) {
			throw new BrandNotFoundRestException();
		}	
	}
	
}
