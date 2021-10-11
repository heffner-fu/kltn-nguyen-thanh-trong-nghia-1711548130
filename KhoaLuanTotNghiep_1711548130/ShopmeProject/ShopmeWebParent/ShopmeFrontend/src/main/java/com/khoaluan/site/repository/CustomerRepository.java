package com.khoaluan.site.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.enums.AuthenticationType;
import com.khoaluan.common.model.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

	public Customer findByEmail(String email);
	
	public Customer findByVerificationCode(String verificationCode);
	
	@Query("UPDATE Customer c SET c.enabled = TRUE, c.verificationCode = null WHERE c.id = ?1")
	@Modifying
	public void enabled(Integer id);
	
	@Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateAuthenticationType(Integer customerId, AuthenticationType authenticationType);
}
