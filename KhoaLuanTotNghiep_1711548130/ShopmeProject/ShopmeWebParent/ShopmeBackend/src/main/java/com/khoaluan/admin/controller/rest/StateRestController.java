package com.khoaluan.admin.controller.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.admin.repository.CountryRepository;
import com.khoaluan.admin.repository.StateRepository;
import com.khoaluan.common.DTO.StateDTO;
import com.khoaluan.common.model.Country;
import com.khoaluan.common.model.State;

@RestController
public class StateRestController {
	
	@Autowired
	StateRepository stateRepository;
	
	@Autowired
	CountryRepository countryRepository;
	
	@GetMapping("/states/list_by_country_be/{id}")
	public List<StateDTO> listAll(@PathVariable("id") Integer id) {
		Country country = countryRepository.findById(id).get();
		List<State> list = stateRepository.findByCountryOrderByNameAsc(country);
		List<StateDTO> response = new ArrayList<>();
		for (State state : list) {
			response.add(new StateDTO(state.getId(), state.getName()));
		}
		return response;
	}
	
	@PostMapping("/states/save")
	public String save(@RequestBody State state) {
		State saved = stateRepository.save(state);
		return String.valueOf(saved.getId()); 
	}
	
	@DeleteMapping("/states/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		stateRepository.deleteById(id);
	}
}
