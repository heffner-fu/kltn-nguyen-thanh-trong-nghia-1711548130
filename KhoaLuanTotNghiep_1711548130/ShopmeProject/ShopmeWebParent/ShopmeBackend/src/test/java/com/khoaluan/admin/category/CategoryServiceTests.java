package com.khoaluan.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.khoaluan.admin.repository.CategoryRepository;
import com.khoaluan.admin.service.CategoryService;
import com.khoaluan.common.model.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

	@MockBean
	CategoryRepository repo;
	
	@InjectMocks
	CategoryService service;
	
	@Test
	public void testChehckUnique() {
		Integer id = null;
		String name = "Computers";
		String alias = "asd";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias);
		assertThat(result).isEqualTo("DuplicateName");

	}
}
