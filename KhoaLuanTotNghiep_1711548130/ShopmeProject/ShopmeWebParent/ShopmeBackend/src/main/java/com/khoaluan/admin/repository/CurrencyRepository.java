package com.khoaluan.admin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.model.Currency;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

	public List<Currency> findAllByOrderByNameAsc();
	
}
