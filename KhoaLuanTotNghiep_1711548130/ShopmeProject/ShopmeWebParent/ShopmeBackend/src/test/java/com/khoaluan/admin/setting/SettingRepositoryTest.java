package com.khoaluan.admin.setting;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.SettingRepository;
import com.khoaluan.common.enums.SettingCategory;
import com.khoaluan.common.model.Setting;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {
	
	@Autowired
	SettingRepository settingRepository;
	
	@Test
	public void testCreateGeneralSettings() {
		Setting siteName = new Setting("SITE_NAME", "Shopme", SettingCategory.GENERAL);
		Setting siteLogo = new Setting("SITE_LOGO", "logo_ecommerce.png", SettingCategory.GENERAL);
		Setting copyright = new Setting("COPYRIGHT", "Nghia Nguyen - Copyright (c) 1711548130", SettingCategory.GENERAL);
		settingRepository.saveAll(List.of(siteName, siteLogo, copyright));

	}
	
	@Test
	public void testCreateCurrencySettings() {
		Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
		Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
		Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
		Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
		Setting decimalDigits = new Setting("DECIMAL_DIGISTS", "2", SettingCategory.CURRENCY);
		Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);
		Setting mailHost = new Setting("MAIL_HOST", "smtp.gmail.com", SettingCategory.MAIL_SERVER);
		Setting mailPort = new Setting("MAIL_PORT", "587", SettingCategory.MAIL_SERVER);
		Setting mailUsername = new Setting("MAIL_USERNAME", "shopme.company@gmail.com", SettingCategory.MAIL_SERVER);
		Setting mailPassword = new Setting("MAIL_PASSWORD", "apppassword", SettingCategory.MAIL_SERVER);
		Setting smtpAuth = new Setting("SMTP_AUTH", "true", SettingCategory.MAIL_SERVER);
		Setting smtpSecured = new Setting("SMTP_SECURED", "true", SettingCategory.MAIL_SERVER);
		Setting mailFrom = new Setting("MAIL_FROM", "shopme.company@gmail.com", SettingCategory.MAIL_SERVER);
		Setting mailSenderName = new Setting("MAIL_SENDER_NAME", "Shopme Company", SettingCategory.MAIL_SERVER);
		Setting customerVerifySubject = new Setting("CUSTOMER_VERIFY_SUBJECT", "email subject",
				SettingCategory.MAIL_TEMPLATES);
		Setting customerVerifyContent = new Setting("CUSTOMER_VERIFY_CONTENT", "email content",
				SettingCategory.MAIL_TEMPLATES);
		Setting orderConfirmation = new Setting("ORDER_CONFIRMATION_SUBJECT", "order email subject",
				SettingCategory.MAIL_TEMPLATES);
		Setting orderConfirmationContent = new Setting("ORDER_CONFIRMATION_CONTENT", "order email content",
				SettingCategory.MAIL_TEMPLATES);
		Setting paypalAPI = new Setting("PAYPAL_API_BASE_URL", "PAYPAL_API_BASE_URL",
				SettingCategory.PAYMENT);
		Setting paypalId = new Setting("PAYPAL_API_CLIENT_ID", "PAYPAL_API_CLIENT_ID",
				SettingCategory.PAYMENT);
		Setting paypalSecrect = new Setting("PAYPAL_API_CLIENT_SECRET", "PAYPAL_API_CLIENT_SECRET",
				SettingCategory.PAYMENT);
		settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits,
				thousandsPointType, mailHost, mailPort, mailUsername, mailPassword, smtpAuth, smtpSecured, mailFrom,
				mailSenderName, customerVerifySubject, customerVerifyContent, orderConfirmation, orderConfirmationContent, 
				paypalAPI, paypalId, paypalSecrect));

	}
	
	@Test
	public void testListSettingCategory() {
		List<Setting> list = settingRepository.findByCategory(SettingCategory.GENERAL);
		
		list.forEach(System.out::println);
	}
}
