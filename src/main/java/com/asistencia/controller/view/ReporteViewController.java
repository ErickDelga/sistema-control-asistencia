package com.asistencia.controller.view;

import com.asistencia.model.Anio;
import com.asistencia.model.Asistencia;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.services.AsistenciaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReporteViewController {

    private final AsistenciaService asistenciaService;

    public ReporteViewController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','SECRETARIA')")
    @GetMapping
    public String verReportes(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            @RequestParam(required = false) String asignatura,
            @RequestParam(required = false) Anio anio,
            @RequestParam(required = false) TipoBachillerato tipoBachillerato,
            @RequestParam(required = false) String seccion,
            Model model) {

        List<Asistencia> resultados = asistenciaService.buscarParaReporte(
                fecha, asignatura, anio, tipoBachillerato, seccion
        );

        model.addAttribute("resultados", resultados);
        model.addAttribute("fecha", fecha);
        model.addAttribute("asignatura", asignatura);
        model.addAttribute("anio", anio);
        model.addAttribute("tipoBachillerato", tipoBachillerato);
        model.addAttribute("seccion", seccion);
        model.addAttribute("anios", Anio.values());
        model.addAttribute("tiposBachillerato", TipoBachillerato.values());

        return "asistencias/reportes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','SECRETARIA')")
    @GetMapping("/descargar-csv")
    public ResponseEntity<byte[]> descargarCsv(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            @RequestParam(required = false) String asignatura,
            @RequestParam(required = false) Anio anio,
            @RequestParam(required = false) TipoBachillerato tipoBachillerato,
            @RequestParam(required = false) String seccion) {

        List<Asistencia> resultados = asistenciaService.buscarParaReporte(
                fecha, asignatura, anio, tipoBachillerato, seccion
        );

        StringBuilder sb = new StringBuilder();

        sb.append("\uFEFF");
        sb.append("ID;FechaHora;Estudiante;Asignatura;Año;TipoBachillerato;Seccion;Estado\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Asistencia a : resultados) {
            sb.append(valor(a.getId()))
                    .append(";")
                    .append(valor(a.getFechaHora() != null ? a.getFechaHora().format(formatter) : ""))
                    .append(";")
                    .append(valor(a.getEstudiante() != null ? a.getEstudiante().getNombreCompleto() : ""))
                    .append(";")
                    .append(valor(a.getClase() != null ? a.getClase().getAsignatura() : ""))
                    .append(";")
                    .append(valor(a.getEstudiante() != null && a.getEstudiante().getAnio() != null ? a.getEstudiante().getAnio().name() : ""))
                    .append(";")
                    .append(valor(a.getEstudiante() != null && a.getEstudiante().getTipoBachillerato() != null ? a.getEstudiante().getTipoBachillerato().name() : ""))
                    .append(";")
                    .append(valor(a.getEstudiante() != null ? a.getEstudiante().getSeccion() : ""))
                    .append(";")
                    .append(valor(a.getEstado() != null ? a.getEstado().name() : ""))
                    .append("\n");
        }

        byte[] contenido = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_asistencias.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(contenido);
    }

    private String valor(Object valor) {
        if (valor == null) {
            return "";
        }
        return String.valueOf(valor).replace(";", ",");
    }
}