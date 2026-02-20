package com.asistencia.controller.view;

import com.asistencia.model.Asistencia;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.services.AsistenciaService;
import com.asistencia.services.EstudianteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/asistencias")
@PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
public class AsistenciaViewController {

    private final AsistenciaService asistenciaService;
    private final EstudianteService estudianteService;

    public AsistenciaViewController(AsistenciaService asistenciaService,
                                    EstudianteService estudianteService) {
        this.asistenciaService = asistenciaService;
        this.estudianteService = estudianteService;
    }

    // ===============================
    // MOSTRAR PANTALLA PRINCIPAL
    // ===============================
    @GetMapping
    public String listar(Model model) {

        LocalDate hoy = LocalDate.now();

        List<Asistencia> asistenciasHoy = asistenciaService.listarPorFecha(hoy);

        long presentes = asistenciasHoy.stream()
                .filter(a -> a.getEstado() == EstadoAsistencia.PRESENTE)
                .count();

        long ausentes = asistenciasHoy.stream()
                .filter(a -> a.getEstado() == EstadoAsistencia.AUSENTE)
                .count();

        long tarde = asistenciasHoy.stream()
                .filter(a -> a.getEstado() == EstadoAsistencia.TARDE)
                .count();

        model.addAttribute("asistencias", asistenciaService.listar());
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        model.addAttribute("presentes", presentes);
        model.addAttribute("ausentes", ausentes);
        model.addAttribute("tarde", tarde);
        model.addAttribute("fechaHoy", hoy);

        return "asistencias";
    }

    // ===============================
    // GUARDAR ASISTENCIA
    // ===============================
    @PostMapping("/guardar")
    public String guardar(@RequestParam Long estudianteId,
                          @RequestParam EstadoAsistencia estado,
                          RedirectAttributes ra) {

        try {
            asistenciaService.registrar(estudianteId, estado);
            ra.addFlashAttribute("ok", "Asistencia registrada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/asistencias";
    }
}
