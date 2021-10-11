package com.khoaluan.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.ProductRepository;
import com.khoaluan.common.model.Brand;
import com.khoaluan.common.model.Category;
import com.khoaluan.common.model.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 11);
		Category category = entityManager.find(Category.class, 4);
		
		Product product = new Product();
		product.setName("Dell XPS 13 9310 i5 1135G7 (70231343)");
		product.setAlias("Dell_XPS_13_9310_i5_1135G7_(70231343)");
		product.setShortDescription("setShortDescription");
		product.setFullDescription("setFullDescription");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(1555);
		product.setCost(1500);
		product.setEnabled(true);
		product.setInStock(true);
		
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product saved = productRepository.save(product);
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testGetAll() {
		Iterable<Product> all = productRepository.findAll();
		all.forEach(System.out::println);
	}
	
	@Test
	public void testSaveProductWithImage() {
		Integer id = 1;
		Product product = productRepository.findById(id).get();
		product.setMainImage("main image.jpg");
		product.addExtraImage("extra image 1.png");
		product.addExtraImage("extra_image_2.png");
		product.addExtraImage("extra-image3.png");
		productRepository.save(product);
	}
}
