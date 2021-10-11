package com.khoaluan.admin.exporter.product;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.khoaluan.admin.exporter.AbstractExporter;
import com.khoaluan.common.model.Product;

public class ProductCsvExporter extends AbstractExporter {

	public void export(List<Product> listProduct, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "product");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"ID", "Name", "Alias", "Enabled", "In-Stock", "Cost", "Price", "Discount Percent", "Length", "Width", "Height", "Weight"};
		String[] fieldMapping = {"id", "name", "alias", "enabled", "inStock", "cost", "price", "discountPercent", "length", "width", "height", "weight"};
		csvWriter.writeHeader(csvHeader);
		for(Product product : listProduct) {
			csvWriter.write(product, fieldMapping);
		}
		csvWriter.close();
	}
	
}
