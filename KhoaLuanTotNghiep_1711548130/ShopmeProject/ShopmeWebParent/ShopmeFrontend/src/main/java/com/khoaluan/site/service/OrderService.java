package com.khoaluan.site.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.khoaluan.common.enums.OrderStatus;
import com.khoaluan.common.enums.PaymentMethod;
import com.khoaluan.common.exception.OrderNotFoundException;
import com.khoaluan.common.model.Address;
import com.khoaluan.common.model.CartItem;
import com.khoaluan.common.model.Customer;
import com.khoaluan.common.model.Order;
import com.khoaluan.common.model.OrderDetail;
import com.khoaluan.common.model.OrderTrack;
import com.khoaluan.common.model.Product;
import com.khoaluan.site.DTO.request.OrderRequest;
import com.khoaluan.site.checkout.CheckoutInfo;
import com.khoaluan.site.repository.OrderRepository;
import com.khoaluan.site.util.Utility;

@Service
public class OrderService {

	@Autowired
	OrderRepository orderRepository;
	
	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
			PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());	
		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			newOrder.setStatus(OrderStatus.PAID);
		} else {
			newOrder.setStatus(OrderStatus.NEW);
		}		
		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getProductCost());
		newOrder.setSubtotal(checkoutInfo.getProductTotal());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTax(0.0f);
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());		
		if (address == null) {
			newOrder.copyAddressFromCustomer();
		} else {
			newOrder.copyShippingAddress(address);
		}	
		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();		
		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();	
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());		
			orderDetails.add(orderDetail);
		}	
		OrderTrack track = new OrderTrack();
		track.setOrder(newOrder);
		track.setStatus(OrderStatus.NEW);
		track.setNotes(OrderStatus.NEW.defaultDescription());
		track.setUpdatedTime(new Date());	
		newOrder.getOrderTracks().add(track);	
		return orderRepository.save(newOrder);
	}
	
	public Page<Order> listForCustomerByPage(Customer customer, int pageNumber, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();		
		Pageable pageable = PageRequest.of(pageNumber - 1, Utility.ORDERS_PER_PAGE, sort);		
		if (keyword != null) {
			return orderRepository.findAll(keyword, customer.getId(), pageable);
		}		
		return orderRepository.findAll(customer.getId(), pageable);
	}
	
	public Order getOrder(Integer id, Customer customer) {
		return orderRepository.findByIdAndCustomer(id, customer);
	}	
	
	public void setOrderReturnRequested(OrderRequest request, Customer customer) 
			throws OrderNotFoundException {
		Order order = orderRepository.findByIdAndCustomer(request.getOrderId(), customer);
		if (order == null) {
			throw new OrderNotFoundException("Order ID " + request.getOrderId() + " not found");
		}		
		if (order.isReturnRequested()) return;		
		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setUpdatedTime(new Date());
		track.setStatus(OrderStatus.RETURN_REQUESTED);
		
		String notes = "Reason: " + request.getReason();
		if (!"".equals(request.getNote())) {
			notes += ". " + request.getNote();
		}		
		track.setNotes(notes);		
		order.getOrderTracks().add(track);
		order.setStatus(OrderStatus.RETURN_REQUESTED);
		orderRepository.save(order);
	}
}
