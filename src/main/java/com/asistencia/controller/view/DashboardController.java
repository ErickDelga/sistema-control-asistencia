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
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "Usuario";

        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");
        boolean esRectoria = tieneRol(auth, "ROLE_RECTORIA");
        boolean esDocente = tieneRol(auth, "ROLE_DOCENTE");
        boolean esSecretaria = tieneRol(auth, "ROLE_SECRETARIA");

        String rolNombre = obtenerNombreRol(esAdmin, esRectoria, esDocente, esSecretaria);

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

    private String obtenerNombreRol(boolean esAdmin,
                                    boolean esRectoria,
                                    boolean esDocente,
                                    boolean esSecretaria) {
        if (esAdmin) {
            return "Administrador";
        }
        if (esRectoria) {
            return "Rectoría";
        }
        if (esDocente) {
            return "Docente";
        }
        if (esSecretaria) {
            return "Secretaría";
        }
        return "Usuario";
    }

    private boolean tieneRol(Authentication auth, String rol) {
        return auth != null
                && auth.getAuthorities() != null
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
    }
}