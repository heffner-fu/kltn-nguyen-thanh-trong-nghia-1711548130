package com.khoaluan.admin.exporter.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.khoaluan.admin.exporter.AbstractExporter;
import com.khoaluan.common.model.Category;

public class CategoryCsvExporter extends AbstractExporter {

	public void export(List<Category> listCategory, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "category");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Category ID", "Name"};
		String[] fieldMapping = {"id", "name"};
		csvWriter.writeHeader(csvHeader);
		for(Category cat : listCategory) {
			csvWriter.write(cat, fieldMapping);
		}
		csvWriter.close();
	}
	
}
