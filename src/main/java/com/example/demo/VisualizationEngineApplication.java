package com.example.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@SpringBootApplication
public class VisualizationEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisualizationEngineApplication.class, args);
	}
}

@Service
class HTMLToPDFService {

	public ByteArrayInputStream convertHTMLToPDF() throws DocumentException, IOException {

		String sampleHtml = "<table style=\"width:100% ;color:red\"><tr><th>Firstname</th><th>Lastname</th><th>Age</th></tr><tr><td>Jill</td><td>Smith</td><td>50</td></tr><tr><td>Eve</td><td>Jackson</td><td>94</td></tr></table>";
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, out);
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		ElementList list = XMLWorkerHelper.parseToElementList(sampleHtml, null);
		for (Element element : list) {
			cell.addElement(element);
		}
		table.addCell(cell);
		document.open();
		document.add(table);

		document.close();
		return new ByteArrayInputStream(out.toByteArray());

	}

}

@Controller
class VisualizationEngineController {

	public HTMLToPDFService htmlToPDFService = null;

	public VisualizationEngineController(HTMLToPDFService htmlToPDFService) {
		this.htmlToPDFService = htmlToPDFService;
	}

	@GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> generateReport() throws DocumentException, IOException {

		ByteArrayInputStream bis = htmlToPDFService.convertHTMLToPDF();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers)
				.body(new InputStreamResource(bis));

	}

}
