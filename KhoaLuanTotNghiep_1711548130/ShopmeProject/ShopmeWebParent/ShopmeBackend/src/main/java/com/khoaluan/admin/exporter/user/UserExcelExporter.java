package com.khoaluan.admin.exporter.user;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.khoaluan.admin.exporter.AbstractExporter;
import com.khoaluan.common.model.User;

public class UserExcelExporter extends AbstractExporter {

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public UserExcelExporter() {
		workbook = new XSSFWorkbook();
	}

	public void export(List<User> listUser, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/octet-stream", ".xlsx", "user");
		writeHeaderLine();
		writeDataLinrs(listUser);
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}
	
	private void writeHeaderLine() {
		sheet = workbook.createSheet("Users");
		XSSFRow row = sheet.createRow(0);
		// Setting font for cell in excel
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		cellStyle.setFont(font);

		createCell(row, 0, "User ID", cellStyle);
		createCell(row, 1, "E-mail", cellStyle);
		createCell(row, 2, "First Name", cellStyle);
		createCell(row, 3, "Last Name", cellStyle);
		createCell(row, 4, "Roles", cellStyle);
		createCell(row, 5, "Enabled", cellStyle);

	}

	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle cellStyle) {
		XSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(cellStyle);
	}

	private void writeDataLinrs(List<User> listUser) {
		int rowIndex = 1;
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		cellStyle.setFont(font);
		for(User user : listUser) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;
			createCell(row, columnIndex, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getId(), cellStyle);	
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);
		}
		
	}

}
