package com.asistencia.controller.view;

import com.asistencia.model.Anio;
import com.asistencia.model.Clase;
import com.asistencia.model.Estudiante;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.services.ClaseService;
import com.asistencia.services.EstudianteService;
import com.asistencia.services.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clases")
public class ClaseViewController {

    private final ClaseService claseService;
    private final EstudianteService estudianteService;
    private final UsuarioService usuarioService;

    public ClaseViewController(ClaseService claseService,
                               EstudianteService estudianteService,
                               UsuarioService usuarioService) {
        this.claseService = claseService;
        this.estudianteService = estudianteService;
        this.usuarioService = usuarioService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clases", claseService.listarParaUsuarioActual());
        return "clases/lista";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @GetMapping("/crear")
    public String crearFormulario(Authentication auth, Model model) {
        model.addAttribute("clase", new Clase());
        model.addAttribute("anios", Anio.values());
        model.addAttribute("tiposBachillerato", TipoBachillerato.values());
        model.addAttribute("docentes", usuarioService.listarDocentes());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("esAdminORectoria", esAdminORectoria(auth));
        return "clases/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Clase clase,
                          RedirectAttributes redirectAttributes) {
        try {
            Clase claseGuardada = claseService.crearClase(clase);
            redirectAttributes.addFlashAttribute("success", "Clase creada correctamente.");
            return "redirect:/clases/" + claseGuardada.getId() + "/asistencias";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/clases";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Authentication auth, Model model) {
        model.addAttribute("clase", claseService.buscarPorId(id));
        model.addAttribute("anios", Anio.values());
        model.addAttribute("tiposBachillerato", TipoBachillerato.values());
        model.addAttribute("docentes", usuarioService.listarDocentes());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("esAdminORectoria", esAdminORectoria(auth));
        return "clases/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA')")
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Clase clase,
                             RedirectAttributes redirectAttributes) {
        try {
            claseService.actualizarClase(clase);
            redirectAttributes.addFlashAttribute("success", "Clase actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clases";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            claseService.eliminarClase(id);
            redirectAttributes.addFlashAttribute("success", "Clase eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la clase.");
        }
        return "redirect:/clases";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/{id}/asistencias")
    public String tomarAsistencia(@PathVariable Long id, Model model) {
        Clase clase = claseService.buscarPorId(id);

        List<Estudiante> estudiantes = estudianteService.buscarPorClase(
                clase.getAnio(),
                clase.getTipoBachillerato(),
                clase.getSeccion()
        );

        model.addAttribute("clase", clase);
        model.addAttribute("estudiantes", estudiantes);

        return "asistencias/tomar-asistencia";
    }

    private boolean esAdminORectoria(Authentication auth) {
        if (auth == null) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_RECTORIA"));
    }
}