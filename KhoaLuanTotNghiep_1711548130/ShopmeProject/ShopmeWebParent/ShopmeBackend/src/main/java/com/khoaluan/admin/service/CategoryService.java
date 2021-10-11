package com.khoaluan.admin.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.khoaluan.admin.controller.CategoryPageInfo;
import com.khoaluan.admin.repository.CategoryRepository;
import com.khoaluan.common.exception.CategoryNotFoundException;
import com.khoaluan.common.model.Category;

@Service
@Transactional
public class CategoryService {
	
	private static final int ROOT_CATEGORIES_PER_PAGE = 4;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	public Category findCategory(Integer id) {
		Optional<Category> findCategory = categoryRepository.findById(id);
		return findCategory.get();
	}
	
	public List<Category> listAll(String sortDir, String keyword) {
		Sort sort = Sort.by("name");
		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}
		List<Category> rootCategories = new ArrayList<>();
		if (keyword != null) {
			rootCategories = categoryRepository.findAllByName(keyword, sort);
		} else {
			rootCategories = categoryRepository.findListRootCategories(sort);
		}		
		return listHierarchicalCategories(rootCategories, sortDir);
	}
	
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNumber, String sortDir) {
		Sort sort = Sort.by("name");
		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}
		Pageable pageable = PageRequest.of(pageNumber - 1, ROOT_CATEGORIES_PER_PAGE, sort);
		Page<Category> pageCategories = categoryRepository.findListRootCategories(pageable);
		//Page<Category> pageCategories = categoryRepository.findAll(pageable);
		List<Category> rootCategories = pageCategories.getContent();
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		return listHierarchicalCategories(rootCategories, sortDir);
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		for(Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			
			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
			
			for(Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}
		return hierarchicalCategories;
	}
	
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSublevel = subLevel+1;
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSublevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSublevel, sortDir);
		}	
	}
	
	public void updateEnabledStatus(Integer id, boolean enabled) {
		categoryRepository.updateEnabledStatus(id, enabled);
	}
	
	public List<Category> getListCategoriesUsedInForm() {
		List<Category> result = new ArrayList<>();
		Iterable<Category> categories = categoryRepository.findListRootCategories(Sort.by("name").ascending());
		for(Category category : categories) {
			if(category.getParent() == null) {
				result.add(Category.copyIdandNameCategory(category));
				
				Set<Category> children = sortSubCategories(category.getChildren());
				
				for(Category subCategory : children) {
					String name = "--" + subCategory.getName();
					result.add(Category.copyIdandNameCategory(subCategory.getId(), name));
					listSubCategoriesInForm(result, subCategory, 1);
				}
			}
		}
		return result;
	}
		
	private void listSubCategoriesInForm(List<Category> result, Category parent, int subLevel) {
		int newSublevel = subLevel+1;
		Set<Category> children = sortSubCategories(parent.getChildren()); // arrange category name children
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSublevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			result.add(Category.copyIdandNameCategory(subCategory.getId(), name));
			listSubCategoriesInForm(result, subCategory, newSublevel);
		}
	}
	
	public Category save(Category category) {
		Category parent = category.getParent();
		if (category.getAlias() == null || category.getAlias().isEmpty()) {
			category.setAlias(category.getName().replace(" ", "-"));
		} 
		category.setAlias(category.getAlias().replace(" ", "-"));
		if (parent != null) {
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		return categoryRepository.save(category);
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		Category categoryByName = categoryRepository.findByName(name);
		if (isCreatingNew) { // Check when create new category
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = categoryRepository.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {// Check when update category
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			Category categoryByAlias = categoryRepository.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		return "OK";
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category o1, Category o2) {
				if (sortDir.equals("asc")) {
					return o1.getName().compareTo(o2.getName());
				} else {
					return o2.getName().compareTo(o1.getName());
				}	
			}
		});
		sortedChildren.addAll(children);
		return sortedChildren;
	}
	
	public void delete(Integer id) throws CategoryNotFoundException {
		Long countId = categoryRepository.countById(id);
		if (countId == null || countId == 0) {
			throw new CategoryNotFoundException("Could not find any category with ID: " + id);
		}
		categoryRepository.deleteById(id);
	}
	
}
