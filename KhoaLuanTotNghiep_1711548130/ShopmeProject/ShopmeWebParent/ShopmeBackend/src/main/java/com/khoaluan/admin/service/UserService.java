package com.khoaluan.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.khoaluan.admin.exception.UserNotFoundException;
import com.khoaluan.admin.repository.RoleRepository;
import com.khoaluan.admin.repository.UserRepository;
import com.khoaluan.admin.util.RandomStringUtil;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.model.Role;
import com.khoaluan.common.model.User;

@Service
@Transactional
public class UserService {
		
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public List<User> getListAll() {
		return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
	}
	
	public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, Util.USER_NUMBER_PER_PAGE, sort);
		if(keyword != null) {
			return userRepository.findAll(keyword, pageable);
		}
		return userRepository.findAll(pageable);
	}
	
	public List<Role> getListRole() {
		return (List<Role>) roleRepository.findAll();
	}
	
	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		if(isUpdatingUser) {
			User existUser = userRepository.findById(user.getId()).get();
			if(user.getPassword().isEmpty()) {
				user.setPassword(existUser.getPassword());
			} else {
				toLowerEmail(user);
				encodePassword(user);
			}
		} else {
			toLowerEmail(user);
			encodePassword(user);
		}
		return userRepository.save(user);
	}
	
	private void toLowerEmail(User user) {
		String lower = user.getEmail().toLowerCase();
		user.setEmail(lower);
	}
	
	private void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}
	
	public boolean isDuplicateEmail(Integer id, String email) {
		User user = userRepository.findByEmail(email);
		if(user == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew) {
			if(user != null) return false;
		} else {
			if(user.getId() != id) {
				return false;
			}
		}
		
		return true;
	}

	public User getUser(Integer id) throws UserNotFoundException {
		try {
			Optional<User> user = userRepository.findById(id);
			return user.get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepository.countById(id);
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		}
		userRepository.deleteById(id);
	}
	
	public void updateEnabledStatus(Integer id, boolean enabled) {
		userRepository.updateEnabledStatus(id, enabled);
	}
	
	public User updateAccount(User form) {
		User user = userRepository.findByEmail(form.getEmail());
		if(!form.getPassword().isEmpty()) {
			user.setPassword(form.getPassword());
			encodePassword(user);
		}
		if(form.getPhoto() != null) {
			user.setPhoto(form.getPhoto());
		}
		user.setFirstName(form.getFirstName());
		user.setLastName(form.getLastName());
		return user;
	}
	
	// Just use only for ccreate data
	public void createDataTest(Integer number) {
		try {
			RandomStringUtil random = new RandomStringUtil();
			for(int i = 0; i <= number; i++) {
				User user = new User();
				user.setEmail(random.randomAlphaNumeric(6) + "@gmail.com");
				user.setFirstName(random.randomAlphaNumeric(5));
				user.setLastName(random.randomAlphaNumeric(5));
				user.setPassword("12345");
				encodePassword(user);
				userRepository.save(user);
			}
		} catch (Exception e) {
			System.out.println("Create data test fail");
		}	
	}
	
}
