package com.khoaluan.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khoaluan.admin.repository.CountryRepository;
import com.khoaluan.common.model.Country;

@SpringBootTest
@AutoConfigureMockMvc
public class countryRestControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	CountryRepository countryRepository;
	
	@Test
	@WithMockUser(username = "ntt.nghia.it@gmail.com", password = "admin", roles = "ADMIN")
	public void testListCountries() throws Exception {
		String url = "/countries/list";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();
		String jsonResponse = result.getResponse().getContentAsString();
		System.out.println(jsonResponse);
	}
	
	@Test
	@WithMockUser(username = "ntt.nghia.it@gmail.com", password = "admin", roles = "ADMIN")
	public void testCreateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		String countryName = "Germany";
		String countryCode = "DE";
		Country country = new Country(countryName, countryCode);
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country))
				.with(csrf()))
				.andDo(print()).andExpect(status().isOk()).andReturn();
		
		String response = result.getResponse().getContentAsString();
		Integer id = Integer.parseInt(response);
		Country c = countryRepository.findById(id).get();
		System.out.println("ID =>>>>>>>>>>" + response);
		
		assertThat(c.getName()).isEqualTo(countryName);
	}
}
