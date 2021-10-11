package com.khoaluan.site.DTO.response;

public class OrderResponse {

	private Integer orderId;
	
	public OrderResponse() { }
	
	public OrderResponse(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
}
