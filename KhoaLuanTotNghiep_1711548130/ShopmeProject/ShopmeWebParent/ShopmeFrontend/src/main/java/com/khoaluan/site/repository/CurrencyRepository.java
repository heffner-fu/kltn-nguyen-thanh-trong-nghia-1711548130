package com.khoaluan.site.repository;

import org.springframework.data.repository.CrudRepository;

import com.khoaluan.common.model.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

}
