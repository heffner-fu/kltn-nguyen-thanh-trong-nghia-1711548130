package com.khoaluan.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.khoaluan.common.model", "com.khoaluan.admin.controller"})
public class ShopmeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopmeBackendApplication.class, args);
	}

}
