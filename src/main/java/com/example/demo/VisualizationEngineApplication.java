package com.example.demo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
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
import com.itextpdf.text.Image;
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
class ChartService {

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

	public ByteArrayInputStream createChartPDF() throws DocumentException, IOException {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, out);
		document.open();
		int width = 300;
		int height = 300;
		JFreeChart chart = getChart();
		BufferedImage bufferedImage = chart.createBufferedImage(width, height);
		Image image = Image.getInstance(writer, bufferedImage, 1.0f);

		
		document.add(image);

		document.close();
		return new ByteArrayInputStream(out.toByteArray());

	}

	public JFreeChart getChart() {

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("One", new Double(43.2));
		dataset.setValue("Two", new Double(10.0));
		dataset.setValue("Three", new Double(27.5));
		dataset.setValue("Four", new Double(17.5));
		dataset.setValue("Five", new Double(11.0));
		dataset.setValue("Six", new Double(19.4));

		// use the ChartFactory to create a pie chart
		JFreeChart chart = ChartFactory.createPieChart("Dummy Data", dataset, true, true, false);
		return chart;
	}

}

@Controller
class VisualizationEngineController {

	public ChartService htmlToPDFService = null;

	public VisualizationEngineController(ChartService htmlToPDFService) {
		this.htmlToPDFService = htmlToPDFService;
	}

	@GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> generateReport() throws DocumentException, IOException {

		ByteArrayInputStream bis = htmlToPDFService.convertHTMLToPDF();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=test.pdf");
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers)
				.body(new InputStreamResource(bis));

	}

	@GetMapping(value = "/report1", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> createChartReport() throws DocumentException, IOException {

		ByteArrayInputStream bis = htmlToPDFService.createChartPDF();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=test.pdf");
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers)
				.body(new InputStreamResource(bis));

	}

}
