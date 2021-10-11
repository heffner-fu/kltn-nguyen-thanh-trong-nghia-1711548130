package com.khoaluan.admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.khoaluan.admin.exporter.category.CategoryCsvExporter;
import com.khoaluan.admin.exporter.category.CategoryExcelExporter;
import com.khoaluan.admin.exporter.category.CategoryPdfExporter;
import com.khoaluan.admin.service.CategoryService;
import com.khoaluan.admin.util.FileUploadUtil;
import com.khoaluan.common.exception.CategoryNotFoundException;
import com.khoaluan.common.model.Category;

@Controller
public class CategoryController {
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("/categories")
	public String getListFirstPage(@Param("sortDir") String sortDir, @Param("keyword") String keyword,Model model) {
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		List<Category> listCategory = categoryService.listAll(sortDir, keyword);
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("listCategory", listCategory);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		return "categories/categories";
		//return getListPage(1, sortDir, model); //using for paging, is fixing
	}
	
//	@GetMapping("/categories/page/{pageNumber}")
//	public String getListPage(@PathVariable(name = "pageNumber") int pageNumber, @Param("sortDir") String sortDir, Model model) {
//		if (sortDir == null || sortDir.isEmpty()) {
//			sortDir = "asc";
//		}
//		CategoryPageInfo pageInfo = new CategoryPageInfo();
//		List<Category> listCategory = categoryService.listByPage(pageInfo, pageNumber, sortDir);
//		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
//		
//		model.addAttribute("totalPage", pageInfo.getTotalPages());
//		model.addAttribute("totalItem", pageInfo.getTotalElements());
//		model.addAttribute("currentPage", pageNumber);
//		model.addAttribute("sortField", "name");
//		model.addAttribute("sortDir", sortDir);
//		model.addAttribute("listCategory", listCategory);
//		model.addAttribute("reverseSortDir", reverseSortDir);
//		return "categories/categories";
//	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateEnableStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
		categoryService.updateEnabledStatus(id, status);
		String result = status ? "Enabled" : "Disabled";
		redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been " + result);
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategory = categoryService.getListCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create New category");
		model.addAttribute("listCategory", listCategory);
		
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, 
			@RequestParam("fileImage") MultipartFile multipartFile, 
			RedirectAttributes redirectAttributes) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = categoryService.save(category);		
			String uploadDir = "../category-images/" + savedCategory.getId(); 
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			categoryService.save(category);
		}
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Category category = categoryService.findCategory(id);
			List<Category> listCategory = categoryService.getListCategoriesUsedInForm();
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit category: " + category.getName());
			model.addAttribute("listCategory", listCategory);
			return "categories/category_form";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) throws CategoryNotFoundException {
		try {
			categoryService.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been delete successfully");
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/csv") 
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Category> listCategory = categoryService.listAll("asc", null);
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategory, response);
	}
	
	@GetMapping("/categories/export/excel") 
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<Category> listCategory = categoryService.listAll("asc", null);
		CategoryExcelExporter exporter = new CategoryExcelExporter();
		exporter.export(listCategory, response);
	}
	
	@GetMapping("/categories/export/pdf") 
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<Category> listCategory = categoryService.listAll("asc", null);
		CategoryPdfExporter exporter = new CategoryPdfExporter();
		exporter.export(listCategory, response);
	}
	
}
