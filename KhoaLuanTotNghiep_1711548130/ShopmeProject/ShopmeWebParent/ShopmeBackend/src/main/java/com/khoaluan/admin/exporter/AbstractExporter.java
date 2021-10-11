package com.khoaluan.admin.exporter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public class AbstractExporter {
	
	public void setResponseHeader(HttpServletResponse response, String contentType, String extension, String name) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd_HH-mm-ss");
		String timeStamp = dateFormat.format(new Date());
		String fileName = name + "_" + timeStamp + extension;
		response.setContentType(contentType);
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		response.setHeader(headerKey, headerValue);
	}
	
}
