package com.khoaluan.site.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.khoaluan.common.model.Category;
import com.khoaluan.site.service.CategoryService;

@Controller
public class MainController {
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("")
	public String viewHomePage(Model model) {
		List<Category> listCategory = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategory", listCategory);
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) { 
			return "login";
		}
		return "redirect:/";
	}

}
