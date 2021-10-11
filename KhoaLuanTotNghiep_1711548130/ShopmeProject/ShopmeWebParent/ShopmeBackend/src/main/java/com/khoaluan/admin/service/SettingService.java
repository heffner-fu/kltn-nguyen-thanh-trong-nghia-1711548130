package com.khoaluan.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khoaluan.admin.repository.CurrencyRepository;
import com.khoaluan.admin.repository.SettingRepository;
import com.khoaluan.admin.setting.GeneralSettingBag;
import com.khoaluan.common.enums.SettingCategory;
import com.khoaluan.common.model.Setting;

@Service
public class SettingService {

	@Autowired
	SettingRepository settingRepository;
	
	@Autowired
	CurrencyRepository currencyRepository;
	
	public List<Setting> listAllSetting() {
		return (List<Setting>) settingRepository.findAll();
	}
	
	public GeneralSettingBag getGeneralSettings() {
		List<Setting> settings = new ArrayList<>();	
		List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);		
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);		
		return new GeneralSettingBag(settings);
	}
	
	public void saveAll(Iterable<Setting> settings) {
		settingRepository.saveAll(settings);
	}
	
	public List<Setting> getMailServerSettings() {
		return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> getMailTemplatesSettings() {
		return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
	
	public List<Setting> getCurrencySettings() {
		return settingRepository.findByCategory(SettingCategory.CURRENCY);
	}
	
	public List<Setting> getPaymentSettings() {
		return settingRepository.findByCategory(SettingCategory.PAYMENT);
	}
}
