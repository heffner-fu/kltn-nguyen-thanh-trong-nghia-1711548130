package com.khoaluan.admin.exporter.user;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.khoaluan.admin.exporter.AbstractExporter;
import com.khoaluan.common.model.User;

public class UserCsvExporter extends AbstractExporter {
	
	public void export(List<User> listUser, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "user");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
		String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
		csvWriter.writeHeader(csvHeader);
		for(User user : listUser) {
			csvWriter.write(user, fieldMapping);
		}
		csvWriter.close();
	}
}
