package com.khoaluan.admin.exporter.category;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.khoaluan.admin.exporter.AbstractExporter;
import com.khoaluan.common.model.Category;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class CategoryPdfExporter extends AbstractExporter {

	public void export(List<Category> listCategory, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf", "category");
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(16);
		font.setColor(Color.BLACK);
		
		Paragraph paragraph = new Paragraph("List categories", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);	
		document.add(paragraph);
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(15);
		table.setWidths(new float[] {1.2f, 3.5f});
		
		writeTableHeader(table);
		writeTableData(table, listCategory);
		document.add(table);
		
		document.close();
	}

	private void writeTableData(PdfPTable table, List<Category> listCategory) {
		for(Category cat : listCategory) {
			table.addCell(String.valueOf(cat.getId()));
			table.addCell(cat.getName());
		}
		
	}

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLACK);
		cell.setPadding(5);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setSize(14);
		font.setColor(Color.WHITE);
		
		cell.setPhrase(new Phrase("Category ID", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Category Name", font));
		table.addCell(cell);
		
	}
	
}
