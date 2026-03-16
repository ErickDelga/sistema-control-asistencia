package com.asistencia.controller.view;

import com.asistencia.model.EstadoAsistencia;
import com.asistencia.services.AsistenciaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    private final AsistenciaService asistenciaService;

    public DashboardController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        cargarDatosDashboard(auth, model);
        return "dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String dashboardAdmin(Authentication auth, Model model) {
        cargarDatosDashboard(auth, model);
        return "dashboard";
    }

    @GetMapping("/rectoria/dashboard")
    public String dashboardRectoria(Authentication auth, Model model) {
        cargarDatosDashboard(auth, model);
        return "dashboard";
    }

    @GetMapping("/docente/dashboard")
    public String dashboardDocente(Authentication auth, Model model) {
        cargarDatosDashboard(auth, model);
        return "dashboard";
    }

    @GetMapping("/secretaria/dashboard")
    public String dashboardSecretaria(Authentication auth, Model model) {
        cargarDatosDashboard(auth, model);
        return "dashboard";
    }

    private void cargarDatosDashboard(Authentication auth, Model model) {
        String username = auth != null ? auth.getName() : "Usuario";

        boolean esAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean esRectoria = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECTORIA"));

        boolean esDocente = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"));

        boolean esSecretaria = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SECRETARIA"));

        String rolNombre = "Usuario";

        if (esAdmin) {
            rolNombre = "Administrador";
        } else if (esRectoria) {
            rolNombre = "Rectoría";
        } else if (esDocente) {
            rolNombre = "Docente";
        } else if (esSecretaria) {
            rolNombre = "Secretaría";
        }

        LocalDate hoy = LocalDate.now();

        long presentes = asistenciaService.contarPorFechaYEstado(hoy, EstadoAsistencia.PRESENTE);
        long ausentes = asistenciaService.contarPorFechaYEstado(hoy, EstadoAsistencia.AUSENTE);
        long tarde = asistenciaService.contarPorFechaYEstado(hoy, EstadoAsistencia.TARDE);
        long totalHoy = presentes + ausentes + tarde;

        model.addAttribute("username", username);
        model.addAttribute("rolNombre", rolNombre);
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("esRectoria", esRectoria);
        model.addAttribute("esDocente", esDocente);
        model.addAttribute("esSecretaria", esSecretaria);

        model.addAttribute("fechaDashboard", hoy);
        model.addAttribute("presentes", presentes);
        model.addAttribute("ausentes", ausentes);
        model.addAttribute("tarde", tarde);
        model.addAttribute("totalHoy", totalHoy);
    }
}