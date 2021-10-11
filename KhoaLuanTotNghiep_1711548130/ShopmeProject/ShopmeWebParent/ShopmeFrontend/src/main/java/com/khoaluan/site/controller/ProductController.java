package com.khoaluan.site.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.khoaluan.common.exception.CategoryNotFoundException;
import com.khoaluan.common.model.Category;
import com.khoaluan.common.model.Product;
import com.khoaluan.site.service.CategoryService;
import com.khoaluan.site.service.ProductService;

@Controller
public class ProductController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
		return viewCategoryByPage(alias, 1, model);
	}

	@GetMapping("/c/{category_alias}/page/{pageNumber}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable("pageNumber") int pageNumber, Model model) {
		try {
			Category category = categoryService.getCategoryByAlias(alias);

			List<Category> listCategoryParent = categoryService.getCategoryParents(category);
			Page<Product> pageProducts = productService.listByCategory(pageNumber, category.getId());
			List<Product> listProduct = pageProducts.getContent();
			
			long start = (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long end = start + ProductService.PRODUCTS_PER_PAGE - 1;
			if (end > pageProducts.getTotalElements())
				end = pageProducts.getTotalElements();

			model.addAttribute("currentPage", pageNumber);
			model.addAttribute("totalPage", pageProducts.getTotalPages());
			model.addAttribute("start", start);
			model.addAttribute("end", end);
			model.addAttribute("totalItem", pageProducts.getTotalElements());
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParent", listCategoryParent);
			model.addAttribute("listProduct", listProduct);
			model.addAttribute("category", category);
			return "product/product_by_category";
		} catch (CategoryNotFoundException e) {
			return "error/404";
		}	
	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
		try {
			Product product = productService.getProduct(alias);
			List<Category> listCategoryParent = categoryService.getCategoryParents(product.getCategory());
			
			model.addAttribute("listCategoryParent", listCategoryParent);
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getShortName());
			return "product/product_detail";
		} catch (Exception e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(@Param("keyword") String keyword,  Model model) {
		return searchByPage(keyword, 1, model);
	}
	
	@GetMapping("/search/page/{pageNumber}")
	public String searchByPage(@Param("keyword") String keyword, @PathVariable("pageNumber") int pageNumber, Model model) {
		Page<Product> pageProducts = productService.search(keyword, pageNumber);
		List<Product> listProduct = pageProducts.getContent();
		
		long start = (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long end = start + ProductService.PRODUCTS_PER_PAGE - 1;
		if (end > pageProducts.getTotalElements())
			end = pageProducts.getTotalElements();

		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", pageProducts.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", pageProducts.getTotalElements());
		model.addAttribute("pageTitle", keyword + " - Search Result");
		model.addAttribute("listProduct", listProduct);
		model.addAttribute("keyword", keyword);
		return "product/search_result";
	}
}
