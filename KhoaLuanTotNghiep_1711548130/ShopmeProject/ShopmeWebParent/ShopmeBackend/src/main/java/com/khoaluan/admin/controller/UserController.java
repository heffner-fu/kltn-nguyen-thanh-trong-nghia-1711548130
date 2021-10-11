package com.khoaluan.admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khoaluan.admin.exception.UserNotFoundException;
import com.khoaluan.admin.exporter.user.UserCsvExporter;
import com.khoaluan.admin.exporter.user.UserExcelExporter;
import com.khoaluan.admin.exporter.user.UserPdfExporter;
import com.khoaluan.admin.service.UserService;
import com.khoaluan.admin.util.FileUploadUtil;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.model.Role;
import com.khoaluan.common.model.User;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/users")
	public String getListFirstPage(Model model) {
		return getListPage(1, model, "id", "asc", null);
	}
	
	@GetMapping("/users/page/{pageNumber}")
	public String getListPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
				@Param("sortField") String sortField, 
				@Param("sortDir") String sortDir, 
				@Param("keyword") String keyword) {
		Page<User> page = userService.listByPage(pageNumber, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
		long start = (pageNumber - 1) * Util.USER_NUMBER_PER_PAGE + 1;
		long end = start + Util.USER_NUMBER_PER_PAGE - 1;
		if(end > page.getTotalElements())
			end = page.getTotalElements();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", page.getTotalElements());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/users");
		
		return "users/users";
	}
	
	

	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRole = userService.getListRole();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRole", listRole);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhoto(multipartFile.getOriginalFilename());
			User savedUser = userService.save(user);
			String uploadDir = "user-images/" + savedUser.getId(); // Save images with folder name is Id for every user
			FileUploadUtil.cleanDirectory(uploadDir); // every directory only save 1 picture, if 1 user update user, old image will be delete.
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); // save image
		} else {
			if(user.getPhoto().isEmpty())
				user.setPhoto(null);
			userService.save(user);
		}
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
		String getEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + getEmail;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			User user = userService.getUser(id);
			List<Role> listRole = userService.getListRole();
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit user: " + user.getId());
			model.addAttribute("listRole", listRole);
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}
	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			userService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been delete successfully");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/users";
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateEnableStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
		userService.updateEnabledStatus(id, status);
		String result = status ? "Enabled" : "Disabled";
		redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been " + result);
		return "redirect:/users";
	}
	
	@GetMapping("/users/export/csv") 
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUser = userService.getListAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUser, response);
	}
	
	@GetMapping("/users/export/excel") 
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUser = userService.getListAll();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUser, response);
	}
	
	@GetMapping("/users/export/pdf") 
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUser = userService.getListAll();
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUser, response);
	}
	
}
