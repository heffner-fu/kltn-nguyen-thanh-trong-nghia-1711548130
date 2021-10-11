package com.khoaluan.admin.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// Check if use had logged and they click button back, they will direct to home page and else they will be direc to Login page
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) { 
			return "login";
		}
		return "redirect:/";
	}

}
