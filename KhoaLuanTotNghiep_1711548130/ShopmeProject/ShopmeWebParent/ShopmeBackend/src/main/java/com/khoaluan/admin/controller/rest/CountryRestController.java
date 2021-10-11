package com.khoaluan.admin.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.khoaluan.admin.repository.CountryRepository;
import com.khoaluan.common.model.Country;

@RestController
public class CountryRestController {

	@Autowired
	CountryRepository countryRepository;
	
	@GetMapping("/countries/list")
	public List<Country> listAll() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	@PostMapping("/countries/save")
	public String save(@RequestBody Country country) {
		Country saved = countryRepository.save(country);
		return String.valueOf(saved.getId()); 
	}
	
	@DeleteMapping("/countries/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		countryRepository.deleteById(id);
	}
}
