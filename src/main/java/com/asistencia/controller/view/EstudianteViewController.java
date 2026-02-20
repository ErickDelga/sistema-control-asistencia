package com.asistencia.controller.view;

import com.asistencia.model.Estudiante;
import com.asistencia.services.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estudiantes")
@PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
public class EstudianteViewController {

    private final EstudianteService estudianteService;

    public EstudianteViewController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    // 🔹 LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        return "estudiantes/listar";
    }

    // 🔹 FORM NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        return "estudiantes/form";
    }

    // 🔹 GUARDAR / ACTUALIZAR
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Estudiante e,
                          BindingResult result,
                          RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "estudiantes/form";
        }

        estudianteService.guardar(e);
        ra.addFlashAttribute("ok", "Estudiante guardado correctamente");
        return "redirect:/estudiantes";
    }

    // 🔹 EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("estudiante", estudianteService.buscarPorId(id));
        return "estudiantes/form";
    }

    // 🔹 ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        estudianteService.eliminar(id);
        ra.addFlashAttribute("ok", "Estudiante eliminado correctamente");
        return "redirect:/estudiantes";
    }
}
