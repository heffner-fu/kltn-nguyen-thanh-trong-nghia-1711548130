package com.khoaluan.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.model.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

	public User findByEmail(String email);
	
	public Long countById(Integer id);
	
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	@Query("SELECT u FROM User u "
			+ "WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName, ' ') LIKE %?1% ")
	public Page<User> findAll(String keyword, Pageable pageable);
	
}
