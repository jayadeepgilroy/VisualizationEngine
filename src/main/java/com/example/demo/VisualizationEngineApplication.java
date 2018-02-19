package com.example.demo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
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
		JFreeChart pieChart = ChartUtil.getPieChart();
		BufferedImage bufferedPieChartImage = pieChart.createBufferedImage(width, height);
		Image pieChartImage = Image.getInstance(writer, bufferedPieChartImage, 1.0f);

		
		document.add(pieChartImage);
		
		JFreeChart barChart = ChartUtil.getBarChart();
		BufferedImage bufferedBarChartImage = barChart.createBufferedImage(width, height);
		Image barChartImage = Image.getInstance(writer, bufferedBarChartImage, 1.0f);

		
		document.add(barChartImage);

		document.close();
		return new ByteArrayInputStream(out.toByteArray());

	}

	

}

class ChartUtil{
	
	public static JFreeChart getPieChart() {

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
	
	public static JFreeChart getBarChart(){
		final String fiat = "FIAT";
	      final String audi = "AUDI";
	      final String ford = "FORD";
	      final String speed = "Speed";
	      final String millage = "Millage";
	      final String userrating = "User Rating";
	      final String safety = "safety";

	      final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	      dataset.addValue( 1.0 , fiat , speed );
	      dataset.addValue( 3.0 , fiat , userrating );
	      dataset.addValue( 5.0 , fiat , millage );
	      dataset.addValue( 5.0 , fiat , safety );

	      dataset.addValue( 5.0 , audi , speed );
	      dataset.addValue( 6.0 , audi , userrating );
	      dataset.addValue( 10.0 , audi , millage );
	      dataset.addValue( 4.0 , audi , safety );

	      dataset.addValue( 4.0 , ford , speed );
	      dataset.addValue( 2.0 , ford , userrating );
	      dataset.addValue( 3.0 , ford , millage );
	      dataset.addValue( 6.0 , ford , safety );

	      JFreeChart barChart = ChartFactory.createBarChart(
	         "CAR USAGE STATIStICS", 
	         "Category", "Score", 
	         dataset,PlotOrientation.VERTICAL, 
	         true, true, false);
	      
	      return barChart;
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
