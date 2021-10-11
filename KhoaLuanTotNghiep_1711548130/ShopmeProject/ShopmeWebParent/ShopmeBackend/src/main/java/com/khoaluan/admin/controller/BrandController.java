package com.khoaluan.admin.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khoaluan.admin.exception.BrandNotFoundException;
import com.khoaluan.admin.service.BrandService;
import com.khoaluan.admin.service.CategoryService;
import com.khoaluan.admin.util.FileUploadUtil;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.model.Brand;
import com.khoaluan.common.model.Category;

@Controller
public class BrandController {

	@Autowired
	BrandService brandService;
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("/brands")
	public String getAll(Model model) {
		return getListPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/brands/page/{pageNumber}")
	public String getListPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
				@Param("sortField") String sortField, 
				@Param("sortDir") String sortDir, 
				@Param("keyword") String keyword) {
		Page<Brand> page = brandService.listByPage(pageNumber, sortField, sortDir, keyword);
		List<Brand> listBrand = page.getContent();
		long start = (pageNumber - 1) * Util.BRAND_PER_PAGE + 1;
		long end = start + Util.BRAND_PER_PAGE - 1;
		if(end > page.getTotalElements())
			end = page.getTotalElements();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", page.getTotalElements());
		model.addAttribute("listBrand", listBrand);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/brands");
		return "brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String createNewBrand(Model model) {
		List<Category> lisCategories = categoryService.getListCategoriesUsedInForm();
		
		model.addAttribute("lisCategories", lisCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create New Brand");
		
		return "/brands/brand_form";
	}
	
	@PostMapping("/brands/save")
	public String saveCategory(Brand brand, 
			@RequestParam("fileImage") MultipartFile multipartFile, 
			RedirectAttributes redirectAttributes) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			Brand savedBrand = brandService.save(brand);		
			String uploadDir = "../brand-logos/" + savedBrand.getId(); 
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			brandService.save(brand);
		}
		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Brand brand = brandService.findBrand(id);
			List<Category> lisCategories = categoryService.getListCategoriesUsedInForm();
			model.addAttribute("brand", brand);
			model.addAttribute("pageTitle", "Edit category: " + brand.getName());
			model.addAttribute("lisCategories", lisCategories);
			return "/brands/brand_form";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) throws BrandNotFoundException {
		try {
			brandService.delete(id);
			String brandDir = "../brand-logos/" + id;
			FileUploadUtil.removeDir(brandDir);
			redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been delete successfully");
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/brands";
	}
	
}
