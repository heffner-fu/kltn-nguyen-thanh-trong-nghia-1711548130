package com.khoaluan.site.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khoaluan.common.model.CartItem;
import com.khoaluan.common.model.Customer;
import com.khoaluan.common.model.Product;
import com.khoaluan.site.repository.CartItemRepository;
import com.khoaluan.site.repository.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {

	@Autowired
	CartItemRepository cartItemRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	public Integer addProduct(Integer productId, Integer quantity, Customer customer) {
		Integer updatedQuantity = quantity;
		Product product = new Product(productId);
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		if (cartItem != null) {
			updatedQuantity = cartItem.getQuantity() + quantity;
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}
		cartItem.setQuantity(updatedQuantity);		
		cartItemRepository.save(cartItem);			
		return updatedQuantity;
	}
	
	public List<CartItem> listCartItem(Customer customer) {
		return cartItemRepository.findByCustomer(customer);
	}
	
	public float updateQuantity(Integer quantity, Customer customer, Integer productId) {
		cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
		Product product = productRepository.findById(productId).get();
		float subtotal = product.getDiscountPrice() * quantity;
		return subtotal;		
	}
	
	public void removeProduct(Integer productId, Customer customer) {
		cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	public void deleteByCustomer(Customer customer) {
		cartItemRepository.deleteByCustomer(customer.getId());
	}
}
