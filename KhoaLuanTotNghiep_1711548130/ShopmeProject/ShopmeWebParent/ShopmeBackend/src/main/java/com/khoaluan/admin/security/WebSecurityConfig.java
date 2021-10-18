package com.khoaluan.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDeatilsService(); // This class had create in package service.
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")		
			.antMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")		
			.antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
			.antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
				.hasAnyAuthority("Admin", "Editor", "Saleperson")
			.antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
				.hasAnyAuthority("Admin", "Editor", "Saleperson", "Shipper")
			.antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
			.antMatchers("/customers/**", "/orders/**", "/get_shipping_cost")
				.hasAnyAuthority("Admin", "Saleperson", "Shipper")
			.antMatchers("/orders_shipper/update/**").hasAuthority("Shipper")
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				// we define email here because spring security have default "username" but login page we are using "email".
				.usernameParameter("email") 
				.permitAll()
			.and().logout().permitAll()
			.and()
				.rememberMe()
					// using remember me for session alive long because when we turn off browser, session will be end.
					.key("qwertyuiopasdfghjklzxcvbnm_1234567890")
					.tokenValiditySeconds(24 * 60 * 60)// Setting time for session alive. at here i set time is 1 hours
					;
		http.headers().frameOptions().sameOrigin();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		//Accept for using ignoring spring security
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**"); 
	}
	
}
