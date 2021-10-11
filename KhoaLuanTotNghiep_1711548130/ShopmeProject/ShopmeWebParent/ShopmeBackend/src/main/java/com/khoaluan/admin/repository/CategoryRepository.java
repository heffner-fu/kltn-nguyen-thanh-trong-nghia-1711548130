package com.khoaluan.admin.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.model.Category;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1% ")
	public List<Category> findAllByName(String keyword, Sort sort);
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findListRootCategories(Sort sort);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public Page<Category> findListRootCategories(Pageable pageable);
	
	
	public Page<Category> findAll(Pageable pageable);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);

	public Long countById(Integer id);
	
}
