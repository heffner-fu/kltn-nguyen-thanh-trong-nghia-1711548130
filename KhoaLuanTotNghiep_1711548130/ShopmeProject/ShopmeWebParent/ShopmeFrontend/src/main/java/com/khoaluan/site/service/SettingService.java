package com.khoaluan.site.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khoaluan.common.enums.SettingCategory;
import com.khoaluan.common.model.Currency;
import com.khoaluan.common.model.Setting;
import com.khoaluan.site.repository.CurrencyRepository;
import com.khoaluan.site.repository.SettingRepository;
import com.khoaluan.site.setting.CurrencySettingBag;
import com.khoaluan.site.setting.EmailSettingBag;
import com.khoaluan.site.setting.PaymentSettingBag;

@Service
public class SettingService {

	@Autowired
	SettingRepository settingRepository;
	
	@Autowired
	CurrencyRepository currencyRepository;
	
	public List<Setting> getGeneralSettings() {	
		return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	
	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));		
		return new EmailSettingBag(settings);
	}
	
	public CurrencySettingBag getCurrencySettings() {
		List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);
		return new CurrencySettingBag(settings);
	}
	
	public PaymentSettingBag getPaymentSettings() {
		List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
		return new PaymentSettingBag(settings);
	}
	
	public String getCurrencyCode() {
		Setting setting = settingRepository.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
		Currency currency = currencyRepository.findById(currencyId).get();		
		return currency.getCode();
	}

}
