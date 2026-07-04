package com.asistencia.controller.view;

import com.asistencia.model.Anio;
import com.asistencia.model.Asistencia;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.services.AsistenciaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaViewController {

    private static final String CARPETA_COMPROBANTES = "uploads/comprobantes";

    private final AsistenciaService asistenciaService;

    public AsistenciaViewController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping
    public String listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String periodo,
            Model model) {

        LocalDate[] rango = resolverPeriodo(periodo, fechaInicio, fechaFin);
        fechaInicio = rango[0];
        fechaFin = rango[1];

        List<Asistencia> asistencias = asistenciaService.buscarConFiltros(
                fechaInicio,
                fechaFin,
                nombres,
                apellidos,
                null,
                null,
                null,
                null
        );

        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("nombres", nombres);
        model.addAttribute("apellidos", apellidos);
        model.addAttribute("periodo", periodo);

        return "asistencias/lista";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @PostMapping("/guardar")
    public String guardarDesdeClase(
            @RequestParam Long estudianteId,
            @RequestParam Long claseId,
            @RequestParam EstadoAsistencia estado,
            @RequestParam(required = false) String motivo,
            @RequestParam(value = "comprobanteFile", required = false) MultipartFile comprobanteFile,
            RedirectAttributes redirectAttributes) {

        try {
            String nombreComprobante = null;

            if (comprobanteFile != null && !comprobanteFile.isEmpty()) {
                nombreComprobante = guardarComprobante(comprobanteFile);
            }

            asistenciaService.registrarPorClase(
                    estudianteId,
                    claseId,
                    estado,
                    motivo,
                    nombreComprobante
            );

            redirectAttributes.addFlashAttribute("success",
                    "Asistencia registrada correctamente.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo registrar la asistencia.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo guardar el comprobante adjunto.");
        }

        return "redirect:/clases/" + claseId + "/asistencias";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @PostMapping("/guardar-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarDesdeClaseAjax(
            @RequestParam Long estudianteId,
            @RequestParam Long claseId,
            @RequestParam EstadoAsistencia estado,
            @RequestParam(required = false) String motivo,
            @RequestParam(value = "comprobanteFile", required = false) MultipartFile comprobanteFile) {

        Map<String, Object> response = new HashMap<>();

        try {
            String nombreComprobante = null;

            if (comprobanteFile != null && !comprobanteFile.isEmpty()) {
                nombreComprobante = guardarComprobante(comprobanteFile);
            }

            asistenciaService.registrarPorClase(
                    estudianteId,
                    claseId,
                    estado,
                    motivo,
                    nombreComprobante
            );

            response.put("success", true);
            response.put("message", "Asistencia registrada correctamente.");
            response.put("minutosBloqueo", 60);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "No se pudo guardar el comprobante adjunto.");
            return ResponseEntity.badRequest().body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "No se pudo registrar la asistencia.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String guardarComprobante(MultipartFile archivo) throws IOException {
        Path carpeta = Paths.get(CARPETA_COMPROBANTES);

        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = obtenerExtension(nombreOriginal);
        String nombreArchivo = System.currentTimeMillis() + "_" + System.nanoTime() + extension;

        Path rutaCompleta = carpeta.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaCompleta);

        return "comprobantes/" + nombreArchivo;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
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