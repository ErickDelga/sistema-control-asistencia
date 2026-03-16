package com.asistencia.controller.api;

import com.asistencia.model.EstadoAsistencia;
import com.asistencia.services.AsistenciaService;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final AsistenciaService service;

    public ReporteController(AsistenciaService service) {
        this.service = service;
    }

    // ===========================
    // EXPORTAR PDF
    // ===========================
    @GetMapping("/pdf")
    public void exportarPDF(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            HttpServletResponse response) throws IOException {

        long presentes = service.contarPorFechaYEstado(fecha, EstadoAsistencia.PRESENTE);
        long ausentes = service.contarPorFechaYEstado(fecha, EstadoAsistencia.AUSENTE);
        long tarde = service.contarPorFechaYEstado(fecha, EstadoAsistencia.TARDE);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_asistencia.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Reporte de Asistencia"));
        document.add(new Paragraph("Fecha: " + fecha));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Presentes: " + presentes));
        document.add(new Paragraph("Ausentes: " + ausentes));
        document.add(new Paragraph("Tarde: " + tarde));

        document.close();
    }

    // ===========================
    // EXPORTAR EXCEL
    // ===========================
    @GetMapping("/excel")
    public void exportarExcel(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            HttpServletResponse response) throws IOException {

        long presentes = service.contarPorFechaYEstado(fecha, EstadoAsistencia.PRESENTE);
        long ausentes = service.contarPorFechaYEstado(fecha, EstadoAsistencia.AUSENTE);
        long tarde = service.contarPorFechaYEstado(fecha, EstadoAsistencia.TARDE);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Presentes");
        header.createCell(2).setCellValue("Ausentes");
        header.createCell(3).setCellValue("Tarde");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(fecha.toString());
        row.createCell(1).setCellValue(presentes);
        row.createCell(2).setCellValue(ausentes);
        row.createCell(3).setCellValue(tarde);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_asistencia.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
