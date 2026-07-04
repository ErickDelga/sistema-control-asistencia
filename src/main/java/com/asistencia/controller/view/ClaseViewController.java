package com.asistencia.controller.view;

import com.asistencia.model.Anio;
import com.asistencia.model.Clase;
import com.asistencia.model.Estudiante;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.model.Usuario;
import com.asistencia.repository.EstudianteRepository;
import com.asistencia.services.AsistenciaService;
import com.asistencia.services.ClaseService;
import com.asistencia.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clases")
public class ClaseViewController {

    private final ClaseService claseService;
    private final UsuarioService usuarioService;
    private final EstudianteRepository estudianteRepository;
    private final AsistenciaService asistenciaService;

    public ClaseViewController(ClaseService claseService,
                               UsuarioService usuarioService,
                               EstudianteRepository estudianteRepository,
                               AsistenciaService asistenciaService) {
        this.claseService = claseService;
        this.usuarioService = usuarioService;
        this.estudianteRepository = estudianteRepository;
        this.asistenciaService = asistenciaService;
    }

    @GetMapping
    public String listar(Model model) {
        Usuario usuarioActual = obtenerUsuarioActual();

        model.addAttribute("clases", claseService.listarParaUsuarioActual());
        model.addAttribute("esDocente", esDocente(usuarioActual));

        return "clases/lista";
    }

    @GetMapping("/crear")
    public String crearFormulario(Model model) {
        Usuario usuarioActual = obtenerUsuarioActual();

        model.addAttribute("clase", new Clase());
        model.addAttribute("docentes", usuarioService.listarDocentes());
        model.addAttribute("anios", Anio.values());
        model.addAttribute("tiposBachillerato", TipoBachillerato.values());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("esAdminORectoria", esAdminORectoria(usuarioActual));

        return "clases/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Clase clase,
                          RedirectAttributes redirectAttributes) {
        try {
            claseService.crearClase(clase);
            redirectAttributes.addFlashAttribute("success", "Clase creada correctamente.");
            return "redirect:/clases";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/clases/crear";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarFormulario(@PathVariable Long id,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioActual = obtenerUsuarioActual();
            Clase clase = claseService.buscarPorId(id);

            model.addAttribute("clase", clase);
            model.addAttribute("docentes", usuarioService.listarDocentes());
            model.addAttribute("anios", Anio.values());
            model.addAttribute("tiposBachillerato", TipoBachillerato.values());
            model.addAttribute("modoEdicion", true);
            model.addAttribute("esAdminORectoria", esAdminORectoria(usuarioActual));

            return "clases/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/clases";
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Clase clase,
                             RedirectAttributes redirectAttributes) {
        try {
            claseService.actualizarClase(clase);
            redirectAttributes.addFlashAttribute("success", "Clase actualizada correctamente.");
            return "redirect:/clases";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/clases/editar/" + clase.getId();
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            claseService.eliminarClase(id);
            redirectAttributes.addFlashAttribute("success", "Clase eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clases";
    }

    @GetMapping("/{id}/asistencias")
    public String verTomarAsistencia(@PathVariable Long id,
                                     @RequestParam(required = false) Boolean autoModal,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Clase clase = claseService.buscarPorId(id);

            List<Estudiante> estudiantes = estudianteRepository.findByAnioAndTipoBachilleratoAndSeccion(
                    clase.getAnio(),
                    clase.getTipoBachillerato(),
                    clase.getSeccion()
            );

            Map<Long, Long> bloqueos = asistenciaService.obtenerBloqueosParaClase(id, estudiantes);

            model.addAttribute("clase", clase);
            model.addAttribute("estudiantes", estudiantes);
            model.addAttribute("bloqueos", bloqueos);
            model.addAttribute("autoModal", Boolean.TRUE.equals(autoModal));

            return "asistencias/tomar-asistencia";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/clases";
        }
    }

    private Usuario obtenerUsuarioActual() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return usuarioService.buscarPorUsername(username);
    }

    private boolean esAdminORectoria(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && ("ADMIN".equals(usuario.getRol().name()) || "RECTORIA".equals(usuario.getRol().name()));
    }

    private boolean esDocente(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && "DOCENTE".equals(usuario.getRol().name());
    }
}