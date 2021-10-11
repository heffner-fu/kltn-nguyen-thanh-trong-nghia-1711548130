package com.khoaluan.site.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.common.exception.CustomerNotFoundException;
import com.khoaluan.common.exception.OrderNotFoundException;
import com.khoaluan.common.model.Customer;
import com.khoaluan.site.DTO.request.OrderRequest;
import com.khoaluan.site.DTO.response.OrderResponse;
import com.khoaluan.site.service.CustomerService;
import com.khoaluan.site.service.OrderService;
import com.khoaluan.site.util.Utility;

@RestController
public class OrderRestController {

	@Autowired 
	OrderService orderService;
	
	@Autowired 
	CustomerService customerService;
	
	@PostMapping("/orders/return")
	public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderRequest returnRequest,
			HttpServletRequest servletRequest) {			
		Customer customer = null;	
		try {
			customer = getAuthenticatedCustomer(servletRequest);
		} catch (CustomerNotFoundException ex) {
			return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
		}	
		try {
			orderService.setOrderReturnRequested(returnRequest, customer);
		} catch (OrderNotFoundException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new OrderResponse(returnRequest.getOrderId()), HttpStatus.OK);
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) 
			throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("No authenticated customer");
		}			
		return customerService.getCustomerByEmail(email);
	}
	
}
