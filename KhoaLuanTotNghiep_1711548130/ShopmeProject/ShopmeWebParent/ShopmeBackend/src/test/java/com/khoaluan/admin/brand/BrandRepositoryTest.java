package com.khoaluan.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.BrandRepository;
import com.khoaluan.common.model.Brand;
import com.khoaluan.common.model.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

	@Autowired
	BrandRepository repository;
	
	@Test
	public void testCreateBrand() {
		Category laptop = new Category(4);
		Category smartphone = new Category(7);
		
		Brand apple = new Brand("Apple");
		apple.getCategories().add(laptop);
		apple.getCategories().add(smartphone);
		Brand saved = repository.save(apple);
		
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateBrand2() {
		Category smartphone = new Category(7);
		
		Brand samsung = new Brand("Samsung");
		samsung.getCategories().add(smartphone);
		Brand saved = repository.save(samsung);
		
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void findAll() {
		List<Brand> result = (List<Brand>) repository.findAll();
		for(Brand a : result) {
			System.out.println(a.toString());
		}
	}
}
