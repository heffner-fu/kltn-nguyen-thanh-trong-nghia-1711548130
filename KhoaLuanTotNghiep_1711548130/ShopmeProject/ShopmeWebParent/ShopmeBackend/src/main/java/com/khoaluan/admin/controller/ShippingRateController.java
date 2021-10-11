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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khoaluan.admin.service.ShippingRateService;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.exception.ShippingRateAlreadyExistsException;
import com.khoaluan.common.exception.ShippingRateNotFoundException;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.ShippingRate;

@Controller
public class ShippingRateController {

private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";
	
	@Autowired 
	ShippingRateService shippingRateService;
	
	@GetMapping("/shipping_rates")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/shipping_rates/page/{pageNumber}")
	public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword) {
		Page<ShippingRate> page = shippingRateService.listByPage(pageNumber, sortField, sortDir, keyword);
		List<ShippingRate> shippingRates = page.getContent();
		long start = (pageNumber - 1) * Util.RATES_PER_PAGE + 1;
		long end = start + Util.RATES_PER_PAGE - 1;
		if(end > page.getTotalElements())
			end = page.getTotalElements();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("shippingRates", shippingRates);
		model.addAttribute("moduleURL", "/shipping_rates");
		return "shipping_rates/shipping_rates";
	}	
	
	@GetMapping("/shipping_rates/new")
	public String newRate(Model model) {
		List<Country> listCountries = shippingRateService.listAllCountries();
		
		model.addAttribute("rate", new ShippingRate());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "New Rate");
		
		return "shipping_rates/shipping_rate_form";		
	}

	@PostMapping("/shipping_rates/save")
	public String saveRate(ShippingRate rate, RedirectAttributes ra) {
		try {
			shippingRateService.save(rate);
			ra.addFlashAttribute("message", "The shipping rate has been saved successfully.");
		} catch (ShippingRateAlreadyExistsException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}
		
	@GetMapping("/shipping_rates/edit/{id}")
	public String editRate(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes ra) {
		try {
			ShippingRate rate = shippingRateService.get(id);
			List<Country> listCountries = shippingRateService.listAllCountries();
			
			model.addAttribute("listCountries", listCountries);			
			model.addAttribute("rate", rate);
			model.addAttribute("pageTitle", "Edit Rate (ID: " + id + ")");
			
			return "shipping_rates/shipping_rate_form";
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
	public String updateCODSupport(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "supported") Boolean supported,
			Model model, RedirectAttributes ra) {
		try {
			shippingRateService.updateCODSupport(id, supported);
			ra.addFlashAttribute("message", "COD support for shipping rate ID " + id + " has been updated.");
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}
	
	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteRate(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes ra) {
		try {
			shippingRateService.delete(id);
			ra.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted.");
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}
}
