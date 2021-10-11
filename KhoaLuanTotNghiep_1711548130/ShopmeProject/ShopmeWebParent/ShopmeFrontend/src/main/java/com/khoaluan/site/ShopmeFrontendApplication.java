package com.khoaluan.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.khoaluan.common.model", "com.khoaluan.site.controller"})
public class ShopmeFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopmeFrontendApplication.class, args);
	}

}
