package com.khoaluan.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.khoaluan.admin.service.ProductService;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.model.Product;

@Controller
public class ProductSearchController {

	@Autowired 
	ProductService productService;
	
	@GetMapping("/orders/search_product")
	public String showSearchProductPage(Model model) {
		return searchProductsByPage(1, model, "id", "asc", null);
	}
	
	@PostMapping("/orders/search_product")
	public String searchProducts(String keyword) {
		return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
	}
	
	@GetMapping("/orders/search_product/page/{pageNumber}")
	public String searchProductsByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model, 
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
				
		Page<Product> page = productService.searchProducts(pageNumber, sortField, sortDir, keyword);
		List<Product> listProducts = page.getContent();
 		long start = (pageNumber - 1) * Util.PRODUCT_PER_PAGE + 1;
		long end = start + Util.PRODUCT_PER_PAGE - 1;
		if(end > page.getTotalElements())
			end = page.getTotalElements();
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", page.getTotalElements());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/orders/search_product");
		return "orders/search_product";
	}
	
}
