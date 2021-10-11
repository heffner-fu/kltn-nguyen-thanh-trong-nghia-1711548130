package com.khoaluan.admin.country;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.CountryRepository;
import com.khoaluan.common.model.Country;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class countryRepositoryTest {

	@Autowired
	CountryRepository countryRepository;
	
	@Test
	public void testCreateCountry() {
		//Country country = countryRepository.save(new Country("United States", "US"));
		//Country country = countryRepository.save(new Country("Vietnam", "VN"));
		Country country = countryRepository.save(new Country("United Kingdom", "UK"));
		assertThat(country).isNotNull();
	}
	
}
