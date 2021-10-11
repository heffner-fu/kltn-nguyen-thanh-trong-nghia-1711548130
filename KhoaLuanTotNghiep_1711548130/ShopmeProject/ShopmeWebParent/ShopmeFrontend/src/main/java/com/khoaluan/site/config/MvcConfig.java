package com.khoaluan.site.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	// Config for expose image for user
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Using for get image Categories
		exposeDirectory("../category-images", registry);
		
		// Using for get image Brands
		exposeDirectory("../brand-logos", registry);
		
		// Using for get image Products
		exposeDirectory("../product-images", registry);
		
		// Using for get image logo of website
		exposeDirectory("../site-logo", registry);
	}
	
	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		String logicalPath = pathPattern.replace("../", "") + "/**";
		registry.addResourceHandler(logicalPath)
							.addResourceLocations("file:/" + absolutePath + "/");
	}
}
