package com.asistencia.controller.view;

import com.asistencia.model.Anio;
import com.asistencia.model.Asistencia;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.services.AsistenciaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReporteViewController {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AsistenciaService asistenciaService;

    public ReporteViewController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','SECRETARIA','DOCENTE')")
    @GetMapping
    public String verReportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String asignatura,
            @RequestParam(required = false) Anio anio,
            @RequestParam(required = false) TipoBachillerato tipoBachillerato,
            @RequestParam(required = false) String seccion,
            @RequestParam(required = false) String periodo,
            Model model) {

        LocalDate[] rango = resolverPeriodo(periodo, fechaInicio, fechaFin);
        fechaInicio = rango[0];
        fechaFin = rango[1];
        seccion = normalizarSeccion(seccion);

        List<Asistencia> asistencias = asistenciaService.buscarConFiltros(
                fechaInicio,
                fechaFin,
                nombres,
                apellidos,
                asignatura,
                anio,
                tipoBachillerato,
                seccion
        );

        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("nombres", nombres);
        model.addAttribute("apellidos", apellidos);
        model.addAttribute("asignatura", asignatura);
        model.addAttribute("anio", anio);
        model.addAttribute("tipoBachillerato", tipoBachillerato);
        model.addAttribute("seccion", seccion);
        model.addAttribute("periodo", periodo);
        model.addAttribute("anios", Anio.values());
        model.addAttribute("tiposBachillerato", TipoBachillerato.values());

        return "asistencias/reportes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','SECRETARIA','DOCENTE')")
    @GetMapping("/csv")
    public void exportarCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String asignatura,
            @RequestParam(required = false) Anio anio,
            @RequestParam(required = false) TipoBachillerato tipoBachillerato,
            @RequestParam(required = false) String seccion,
            @RequestParam(required = false) String periodo,
            HttpServletResponse response) throws Exception {

        LocalDate[] rango = resolverPeriodo(periodo, fechaInicio, fechaFin);
        fechaInicio = rango[0];
        fechaFin = rango[1];
        seccion = normalizarSeccion(seccion);

        List<Asistencia> asistencias = asistenciaService.buscarConFiltros(
                fechaInicio,
                fechaFin,
                nombres,
                apellidos,
                asignatura,
                anio,
                tipoBachillerato,
                seccion
        );

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_asistencias.csv");

        PrintWriter writer = response.getWriter();
        writer.println('\uFEFF' + "Fecha y hora,Nombres,Apellidos,Asignatura,Año,Tipo Bachillerato,Sección,Estado,Motivo");

        for (Asistencia a : asistencias) {
            String fechaHora = a.getFechaHora() != null
                    ? a.getFechaHora().format(FORMATO_FECHA_HORA)
                    : "";

            String nombresCsv = a.getEstudiante() != null
                    ? valorSeguro(a.getEstudiante().getNombres())
                    : "";

            String apellidosCsv = a.getEstudiante() != null
                    ? valorSeguro(a.getEstudiante().getApellidos())
                    : "";

            String asignaturaCsv = a.getClase() != null
                    ? valorSeguro(a.getClase().getAsignatura())
                    : "";

            String anioCsv = a.getEstudiante() != null && a.getEstudiante().getAnio() != null
                    ? a.getEstudiante().getAnio().name()
                    : "";

            String tipoCsv = a.getEstudiante() != null && a.getEstudiante().getTipoBachillerato() != null
                    ? valorSeguro(a.getEstudiante().getTipoBachillerato().getNombreVisible())
                    : "";

            String seccionCsv = a.getEstudiante() != null
                    ? valorSeguro(a.getEstudiante().getSeccion())
                    : "";

            String estadoCsv = a.getEstado() != null
                    ? a.getEstado().name()
                    : "";

            String motivoCsv = valorSeguro(a.getMotivo());

            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    escapar(fechaHora),
                    escapar(nombresCsv),
                    escapar(apellidosCsv),
                    escapar(asignaturaCsv),
                    escapar(anioCsv),
                    escapar(tipoCsv),
                    escapar(seccionCsv),
                    escapar(estadoCsv),
                    escapar(motivoCsv)
            );
        }

        writer.flush();
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\"", "\"\"")
                .replace("\r", " ")
                .replace("\n", " ");
    }

    private String normalizarSeccion(String seccion) {
        if (seccion == null || seccion.isBlank()) {
            return null;
        }

        String valor = seccion.trim().toUpperCase();
        return valor.length() > 1 ? valor.substring(0, 1) : valor;
    }

    private LocalDate[] resolverPeriodo(String periodo, LocalDate fechaInicio, LocalDate fechaFin) {
        if (periodo == null || periodo.isBlank()) {
            return new LocalDate[]{fechaInicio, fechaFin};
        }

        LocalDate hoy = LocalDate.now();

        return switch (periodo) {
            case "HOY" -> new LocalDate[]{hoy, hoy};
            case "SEMANA" -> new LocalDate[]{hoy.with(DayOfWeek.MONDAY), hoy};
            case "MES" -> new LocalDate[]{YearMonth.now().atDay(1), hoy};
            default -> new LocalDate[]{fechaInicio, fechaFin};
        };
    }
}