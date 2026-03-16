package com.asistencia.controller.view;

import com.asistencia.model.Asistencia;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.services.AsistenciaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaViewController {

    private final AsistenciaService asistenciaService;

    public AsistenciaViewController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping
    public String listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            Model model) {

        List<Asistencia> asistencias = (fecha != null)
                ? asistenciaService.listarPorFecha(fecha)
                : asistenciaService.listar();

        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fechaSeleccionada", fecha);

        return "asistencias/lista";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @PostMapping("/guardar")
    public String guardarDesdeClase(
            @RequestParam Long estudianteId,
            @RequestParam Long claseId,
            @RequestParam EstadoAsistencia estado,
            RedirectAttributes redirectAttributes) {

        try {
            asistenciaService.registrarPorClase(estudianteId, claseId, estado);
            redirectAttributes.addFlashAttribute("success",
                    "Asistencia registrada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo registrar la asistencia.");
        }

        return "redirect:/clases/" + claseId + "/asistencias";
    }
}