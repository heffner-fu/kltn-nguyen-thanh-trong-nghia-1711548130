package com.khoaluan.admin.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khoaluan.admin.security.ShopmeUserDetails;
import com.khoaluan.admin.service.UserService;
import com.khoaluan.admin.util.FileUploadUtil;
import com.khoaluan.common.model.User;

@Controller
public class AccountController {

	@Autowired
	UserService userService;
	
	@GetMapping("/account")
	public String viewDetailsAccount(@AuthenticationPrincipal ShopmeUserDetails loggedUSer, Model model) {
		String email = loggedUSer.getUsername();
		User user = userService.getUserByEmail(email);
		model.addAttribute("user", user);
		return "/users/account_form";
	}
	
	@PostMapping("/account/update")
	public String updateAccount(User user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShopmeUserDetails loggedUSer,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhoto(multipartFile.getOriginalFilename());
			User savedUser = userService.updateAccount(user);
			String uploadDir = "user_images/" + savedUser.getId();
			FileUploadUtil.cleanDirectory(uploadDir); 
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); 
		} else {
			if(user.getPhoto().isEmpty())
				user.setPhoto(null);
			userService.updateAccount(user);
		}
		loggedUSer.setFirstName(user.getFirstName());
		loggedUSer.setLastName(user.getLastName());
		redirectAttributes.addFlashAttribute("message", "Your Account update Successfully");
		return "redirect:/account";
	}
}
