package com.khoaluan.admin.country;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.CountryRepository;
import com.khoaluan.admin.repository.StateRepository;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.State;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class stateRepositoryTest {

	@Autowired
	StateRepository stateRepository;
	
	@Autowired
	CountryRepository countryRepository; 
	
	@Test
	public void testCreateState() {
		Country country = countryRepository.findById(1).get();
		State state1 = new State("Texas", country);
		State state2 = new State("New York", country);
		State state3 = new State("Washington", country);
		
		stateRepository.saveAll(List.of(state1, state2, state3));
	}
}
