package com.khoaluan.admin.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khoaluan.admin.security.ShopmeUserDetails;
import com.khoaluan.admin.service.OrderService;
import com.khoaluan.admin.service.SettingService;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.enums.OrderStatus;
import com.khoaluan.common.exception.OrderNotFoundException;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.Order;
import com.khoaluan.common.model.OrderDetail;
import com.khoaluan.common.model.OrderTrack;
import com.khoaluan.common.model.Product;
import com.khoaluan.common.model.Setting;

@Controller
public class OrderController {

	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

	@Autowired
	OrderService orderService;

	@Autowired
	SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/orders/page/{pageNumber}")
	public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword,
			HttpServletRequest request, @AuthenticationPrincipal ShopmeUserDetails loggedUser) {

		Page<Order> page = orderService.listByPage(pageNumber, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();
		long start = (pageNumber - 1) * Util.ORDERS_PER_PAGE + 1;
		long end = start + Util.ORDERS_PER_PAGE - 1;
		if(end > page.getTotalElements())
			end = page.getTotalElements();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", page.getTotalElements());
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/orders");
		loadCurrencySetting(request);		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}		
		return "orders/orders";
	}

	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();

		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request, @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);

			boolean isVisibleForAdminOrSalesperson = false;

			if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
				isVisibleForAdminOrSalesperson = true;
			}

			model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
			model.addAttribute("order", order);

			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}

	}

	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			orderService.delete(id);
			;
			ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}

		return defaultRedirectURL;
	}

	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);
			List<Country> listCountries = orderService.listAllCountries();

			model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);

			return "orders/order_form";

		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}

	}

	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);

		updateProductDetails(order, request);
		updateOrderTracks(order, request);

		orderService.save(order);

		ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");

		return defaultRedirectURL;
	}

	private void updateOrderTracks(Order order, HttpServletRequest request) {
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackNotes = request.getParameterValues("trackNotes");

		List<OrderTrack> orderTracks = order.getOrderTracks();
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack trackRecord = new OrderTrack();

			Integer trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
				trackRecord.setId(trackId);
			}

			trackRecord.setOrder(order);
			trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			trackRecord.setNotes(trackNotes[i]);

			try {
				trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			orderTracks.add(trackRecord);
		}
	}

	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productDetailCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCosts = request.getParameterValues("productShipCost");

		Set<OrderDetail> orderDetails = order.getOrderDetails();

		for (int i = 0; i < detailIds.length; i++) {
			OrderDetail orderDetail = new OrderDetail();
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				orderDetail.setId(detailId);
			}
			orderDetail.setOrder(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
			orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
			orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
			orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
			orderDetails.add(orderDetail);
		}
	}

}
