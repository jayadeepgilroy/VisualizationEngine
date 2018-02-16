package com.example.demo;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
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
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

@SpringBootApplication
public class VisualizationEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisualizationEngineApplication.class, args);
	}
}

@Service
class HTMLToPDFService {

	public ByteInputStream convertHTMLToPDF() throws DocumentException, IOException {

		String sampleHtml = "<!DOCTYPE html><html><body><h1>My First Heading</h1><p>My first paragraph.</p></body></html>";
		ByteOutputStream out = new ByteOutputStream();
		Document doc = new Document();
		PdfWriter.getInstance(doc, out);
		doc.open();
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		ElementList list = XMLWorkerHelper.parseToElementList(sampleHtml, null);
		for (Element element : list) {
			cell.addElement(element);
		}
		table.addCell(cell);
		doc.add(table);
		doc.close();

		return new ByteInputStream(out.getBytes(), 100);
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

		ByteInputStream bis = htmlToPDFService.convertHTMLToPDF();
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));

	}

}
