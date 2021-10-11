package com.khoaluan.site.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.khoaluan.common.exception.CustomerNotFoundException;
import com.khoaluan.common.model.Address;
import com.khoaluan.common.model.CartItem;
import com.khoaluan.common.model.Customer;
import com.khoaluan.common.model.ShippingRate;
import com.khoaluan.site.service.AddressService;
import com.khoaluan.site.service.CustomerService;
import com.khoaluan.site.service.ShippingRateService;
import com.khoaluan.site.service.ShoppingCartService;
import com.khoaluan.site.util.Utility;

@Controller
public class ShoppingCartController {

	@Autowired
	ShoppingCartService shoppingCartService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	AddressService addressService;
	
	@Autowired
	ShippingRateService shippingRateService;
	
	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) throws CustomerNotFoundException {
		Customer customer = getAuthenticatedCustomer(request);
		List<CartItem> cartItems = shoppingCartService.listCartItem(customer);
		
		float estimatedTotal = 0.0F;
		for (CartItem item : cartItems) {
			estimatedTotal += item.getSubtotal();
		}
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressDefault = false;
		if (defaultAddress != null) {
			shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
		} else {
			usePrimaryAddressDefault = true;
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
		}
		
		model.addAttribute("usePrimaryAddressDefault", usePrimaryAddressDefault);
		model.addAttribute("shippingSupported", shippingRate != null);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		return "cart/shopping_cart";
	}
	
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
}
