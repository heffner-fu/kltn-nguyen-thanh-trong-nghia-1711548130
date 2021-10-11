package com.khoaluan.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khoaluan.admin.repository.CurrencyRepository;
import com.khoaluan.admin.service.SettingService;
import com.khoaluan.admin.setting.GeneralSettingBag;
import com.khoaluan.admin.util.FileUploadUtil;
import com.khoaluan.common.model.Currency;
import com.khoaluan.common.model.Setting;

@Controller
public class SettingController {

	@Autowired
	SettingService settingService;
	
	@Autowired
	CurrencyRepository currencyRepository;
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> listSetting = settingService.listAllSetting();
		List<Currency> listCurrency = currencyRepository.findAllByOrderByNameAsc();
		

		model.addAttribute("listCurrency", listCurrency);
		for (Setting setting : listSetting) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		return "setting/settings";
	}
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
					HttpServletRequest request, RedirectAttributes ra) throws IOException {
		GeneralSettingBag settingBag = settingService.getGeneralSettings();
		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(request, settingBag);
		updateSettingValueFromForm(request, settingBag.list());
		ra.addFlashAttribute("message", "General settings have been saved");
		
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra) throws IOException {
		List<Setting> mailTemplatesSettings = settingService.getMailServerSettings();
		updateSettingValueFromForm(request, mailTemplatesSettings);
		ra.addFlashAttribute("message", "Mail Server settings have been saved");
		
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplatesSettings(HttpServletRequest request, RedirectAttributes ra) throws IOException {
		List<Setting> mailServerSettings = settingService.getMailTemplatesSettings();
		updateSettingValueFromForm(request, mailServerSettings);
		ra.addFlashAttribute("message", "Mail Templates settings have been saved");
		
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_payment")
	public String savePaymentSetttings(HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> paymentSettings = settingService.getPaymentSettings();
		updateSettingValueFromForm(request, paymentSettings);
		
		ra.addFlashAttribute("message", "Payment settings have been saved");
		
		return "redirect:/settings#payment";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			String uploadDir = "../site-logo/";
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
	}
	
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findCurrency = currencyRepository.findById(currencyId);
		if (findCurrency.isPresent()) {
			Currency currency = findCurrency.get();
			String symbol = currency.getSymbol();
			settingBag.updateCurrencySymbol(symbol);
		}
	}
	
	private void updateSettingValueFromForm(HttpServletRequest request, List<Setting> listSetting) {
		for (Setting setting : listSetting) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}
		settingService.saveAll(listSetting);
	}
	
}
