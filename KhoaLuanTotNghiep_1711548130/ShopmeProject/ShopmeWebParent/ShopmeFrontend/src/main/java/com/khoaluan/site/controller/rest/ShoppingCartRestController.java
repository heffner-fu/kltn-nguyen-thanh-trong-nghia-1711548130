package com.khoaluan.site.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.common.exception.CustomerNotFoundException;
import com.khoaluan.common.model.Customer;
import com.khoaluan.site.service.CustomerService;
import com.khoaluan.site.service.ShoppingCartService;
import com.khoaluan.site.util.Utility;

@RestController
public class ShoppingCartRestController {

	@Autowired
	ShoppingCartService shoppingCartService;
	
	@Autowired
	CustomerService customerService;
	
	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable(name = "productId") Integer productId, 
							@PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			Integer updateQuantity = shoppingCartService.addProduct(productId, quantity, customer);					
			return updateQuantity + " item(s) of this product were added to your shopping cart.";
		} catch (CustomerNotFoundException e) {
			return "You must login to add this product to cart.";
		}
	}
	
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable(name = "productId") Integer productId, 
							@PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subtotal = shoppingCartService.updateQuantity(quantity, customer, productId);
			return String.valueOf(subtotal);
		} catch (CustomerNotFoundException e) {
			return "You must login to change quantity of product.";
		}
	}
	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable(name = "productId") Integer productId, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			shoppingCartService.removeProduct(productId, customer);
			return "The product has been removed from your shopping cart.";
		} catch (CustomerNotFoundException e) {
			return "You must login to remove product.";
		}
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("No authenticated customer");
		}
		
		return customerService.getCustomerByEmail(email);
	}
	
}
