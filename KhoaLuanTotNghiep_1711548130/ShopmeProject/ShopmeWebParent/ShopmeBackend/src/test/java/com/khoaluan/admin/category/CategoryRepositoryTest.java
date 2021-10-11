package com.khoaluan.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.CategoryRepository;
import com.khoaluan.common.model.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

	@Autowired
	CategoryRepository categoryRepository;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		categoryRepository.save(category);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(4);
		Category subCategory = new Category("Gaming Laptops", parent);
		categoryRepository.save(subCategory);
	}
	
	@Test
	public void testGetCategory() {
		Category category = categoryRepository.findById(2).get();
		System.out.println(category.getName());
		Set<Category> children = category.getChildren();
		for(Category sub : children) {
			System.out.println("--" + sub.getName());
		}
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> list = categoryRepository.findAll();
		for(Category sub : list) {
			//System.out.println(sub.toString());
			if(sub.getParent() == null) {
				System.out.println(sub.getName());
				
				Set<Category> children = sub.getChildren();
				
				for(Category sub2 : children) {
					System.out.println("--" + sub2.getName());
					printChildren(sub2, 1);
				}
			}
		}
	}
	
	@Test
	public void getallasd() {
		List<Category> list = (List<Category>) categoryRepository.findAll();
		for(Category a : list) {
			System.out.println(a.getName());
		}
		System.out.println("Notthing");
	}
	
	@Test
	public void updateStatus() {
		categoryRepository.updateEnabledStatus(1, true);
	}
	
	private void printChildren(Category parent, int subLevel) {
		int newSublevel = subLevel+1;
		for(Category sub : parent.getChildren()) {
			for(int i = 0; i < newSublevel; i++) {
				System.out.print("--");
			}
			System.out.println(sub.getName());
			printChildren(sub, newSublevel);
		}
	}
	
	@Test
	public void findName() {
		Category category = categoryRepository.findByName("Computers");
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo("Computers");
	
	}
	
	@Test
	public void findAlias() {
		Category category = categoryRepository.findByAlias("Computers");
		assertThat(category).isNotNull();
		assertThat(category.getAlias()).isEqualTo("Computers");
	
	}
	
}
