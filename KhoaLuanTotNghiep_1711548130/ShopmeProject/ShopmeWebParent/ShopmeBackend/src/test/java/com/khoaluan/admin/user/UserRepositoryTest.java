package com.khoaluan.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.UserRepository;
import com.khoaluan.admin.service.UserService;
import com.khoaluan.common.model.Role;
import com.khoaluan.common.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	TestEntityManager entityManager; 
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1); // Sử dụng hàm này lấy quyền admin đã được tạo từ bảng Role và có id là 1
		User user = new User("a3@gmail.com", "admin", "Nghia", "Trong");
		user.addRole(roleAdmin);
		User a = userRepository.save(user);
		assertThat(a.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testDuplicateEmail() {
		String email = "ntt.nghia.it@gmail.com";
		User user = userRepository.findByEmail(email);
		if(user != null)
			System.out.println("Co ton tai");
		else
			System.out.println("ko co");
	}
	
	@Test
	public void countById() {
		Long count = userRepository.countById(1);
		
		assertThat(count).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void updateEnabledStatus() {
		userRepository.updateEnabledStatus(1, false);
	}
	
}
