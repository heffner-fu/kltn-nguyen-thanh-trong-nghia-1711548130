package com.khoaluan.admin.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.khoaluan.admin.repository.RoleRepository;
import com.khoaluan.common.model.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {
	
	@Autowired
	RoleRepository roleRepository;
	
	@Test
	public void testCreateRoles() {
		Role roleAdmin = new Role("Admin", "Manage everything");
		Role roleSaleperson = new Role("Saleperson", "Manage product price, customers, shipping, orders and sales report");	
		Role roleEditor = new Role("Editor", "Manage categories, brands, products, articles and menus");
		Role roleShiper = new Role("Shipper", "View products, view orders, and update order status");
		Role roleAssistant = new Role("Assistant", "Manage questions ans reviews");
		roleRepository.saveAll(List.of(roleAdmin, roleAssistant, roleEditor, roleSaleperson, roleShiper));
	}
	
}
