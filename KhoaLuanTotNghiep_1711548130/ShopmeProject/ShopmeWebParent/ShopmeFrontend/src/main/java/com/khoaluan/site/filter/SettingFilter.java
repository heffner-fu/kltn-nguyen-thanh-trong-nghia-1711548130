package com.khoaluan.site.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.khoaluan.common.model.Setting;
import com.khoaluan.site.service.SettingService;

@Component
public class SettingFilter implements Filter {

	@Autowired
	SettingService settingService;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURI().toString();
		if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg")) {
			chain.doFilter(request, response);
			return;
		}
		List<Setting> generalSetting = settingService.getGeneralSettings();
		generalSetting.forEach(setting -> {
//			System.out.println(setting);
			request.setAttribute(setting.getKey(), setting.getValue());
		});
		
//		System.out.println(url);
		
		chain.doFilter(request, response);
	}

}
