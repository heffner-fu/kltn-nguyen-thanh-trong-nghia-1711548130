package com.khoaluan.site.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.model.Product;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE p.enabled = TRUE "
			+ "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%) "
			+ "ORDER BY p.name ASC")
	public Page<Product> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);
	
	public Product findByAlias(String alias);
	
	@Query(value = "SELECT * FROM products WHERE enabled = TRUE AND "
			+ "MATCH (name, short_description, full_description) AGAINST (?1)", nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
}
