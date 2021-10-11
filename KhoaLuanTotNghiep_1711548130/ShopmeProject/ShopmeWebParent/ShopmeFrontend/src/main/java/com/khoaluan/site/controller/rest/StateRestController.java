package com.khoaluan.site.controller.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.common.DTO.StateDTO;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.State;
import com.khoaluan.site.repository.CountryRepository;
import com.khoaluan.site.repository.StateRepository;

@RestController
public class StateRestController {
	
	@Autowired
	StateRepository stateRepository;
	
	@Autowired
	CountryRepository countryRepository;
	
	@GetMapping("/setting/list_state_by_country/{id}")
	public List<StateDTO> listAll(@PathVariable("id") Integer id) {
		Country country = countryRepository.findById(id).get();
		List<State> list = stateRepository.findByCountryOrderByNameAsc(country);
		List<StateDTO> response = new ArrayList<>();
		for (State state : list) {
			response.add(new StateDTO(state.getId(), state.getName()));
		}
		return response;
	}
	
}
