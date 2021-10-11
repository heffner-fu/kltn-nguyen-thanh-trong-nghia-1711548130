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

import com.khoaluan.admin.service.CustomerService;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.exception.CustomerNotFoundException;
import com.khoaluan.common.exception.ProductNotFoundException;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.Customer;

@Controller
public class CustomerController {

	@Autowired
	CustomerService customerService;
	
	@GetMapping("/customers")
	public String getFirstPage(Model model) {
		return getListPage(1, model, "id", "asc", null);
	}
	
	@GetMapping("/customers/page/{pageNumber}")
	public String getListPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
				@Param("sortField") String sortField, 
				@Param("sortDir") String sortDir, 
				@Param("keyword") String keyword) {
		Page<Customer> page = customerService.listByPage(pageNumber, sortField, sortDir, keyword);
		List<Customer> listCustomer = page.getContent();
		long start = (pageNumber - 1) * Util.CUSTOMERS_PER_PAGE + 1;
		long end = start + Util.CUSTOMERS_PER_PAGE - 1;
		if(end > page.getTotalElements())
			end = page.getTotalElements();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", page.getTotalElements());
		model.addAttribute("listCustomer", listCustomer);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/customers");
		return "customers/customers";
	}
	
	@GetMapping("/customers/new")
	public String create(Model model) {
		List<Country> listCountries = customerService.listAllCountries();
		Customer customer = new Customer();
		customer.setEnabled(true);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Registation");
		model.addAttribute("customer", customer);
		
		return "customers/customers_form";
	}
	
	@PostMapping("/customers/save")
	public String createCustomer(Customer customer, Model model, RedirectAttributes redirectAttributes) {	
		customerService.save(customer);
		redirectAttributes.addFlashAttribute("message", "The customer has been saved successfully");
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateEnableStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
		customerService.updateEnabledStatus(id, status);
		String result = status ? "Enabled" : "Disabled";
		redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been " + result);
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}") 
	public String viewCustomerDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Customer customer = customerService.get(id);			
			model.addAttribute("customer", customer);						
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";
		}
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Customer customer = customerService.get(id);
			List<Country> listCountries = customerService.listAllCountries();		
			model.addAttribute("listCountries", listCountries);
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));	
			return "customers/customers_form";
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";
		}
	}
	
	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) throws ProductNotFoundException {
		try {
			customerService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The customer ID " + id + " has been delete successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/customers";
	}
	
}
