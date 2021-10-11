package com.khoaluan.site.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khoaluan.common.exception.CategoryNotFoundException;
import com.khoaluan.common.model.Category;
import com.khoaluan.site.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	CategoryRepository categoryRepository;
	
	public List<Category> listNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();
		
		List<Category> listEnabledCategories = categoryRepository.findAllEnabled();
		
		listEnabledCategories.forEach(category -> {
			Set<Category> children = category.getChildren();
			if (children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		});
		
		return listNoChildrenCategories;
	}
	
	public Category getCategoryByAlias(String alias) throws CategoryNotFoundException {
		Category category = categoryRepository.findByAliasEnabled(alias);
		if (category == null) {
			throw new CategoryNotFoundException("Could not found any category with alias " + alias);
		}
		return category;
	}
	
	public List<Category> getCategoryParents(Category child) {
		List<Category> listParent = new ArrayList<>();
		Category parent = child.getParent();
		while (parent != null) {
			listParent.add(0, parent);
			parent = parent.getParent();
		}
		listParent.add(child);
		
		return listParent;
	}
}
