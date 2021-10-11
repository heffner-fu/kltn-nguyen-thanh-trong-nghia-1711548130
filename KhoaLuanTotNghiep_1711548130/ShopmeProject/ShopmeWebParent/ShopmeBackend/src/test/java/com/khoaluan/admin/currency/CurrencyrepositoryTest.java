package com.khoaluan.admin.currency;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.CurrencyRepository;
import com.khoaluan.common.model.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyrepositoryTest {
	
	@Autowired
	CurrencyRepository currencyRepository;
	
	@Test
	public void testCreateCurrncy() {
		Currency num1 = new Currency("Vietnamese đồng", "₫", "VND");
		Currency num2 = new Currency("United States Dollar", "$", "USD");
		Currency num3 = new Currency("Japanese Yen", "¥", "JPY");
		Currency num4 = new Currency("Euro", "€", "EUR");
		Currency num5 = new Currency("Chinese Yuan", "¥", "CNY");
		currencyRepository.saveAll(List.of(num1, num2, num3, num4, num5));
	}
	
	@Test
	public void testGetList() {
		List<Currency> list = currencyRepository.findAllByOrderByNameAsc();
		list.forEach(System.out::println);
//		for (Currency a : list) {
//			System.out.println("Code: " + a.getCode() + " | Symbol: " + a.getSymbol() + " | Name: " + a.getName());
//		}
	}
}
