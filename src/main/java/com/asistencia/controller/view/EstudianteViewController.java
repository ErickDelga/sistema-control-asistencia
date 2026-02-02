package com.asistencia.controller.view;

import com.asistencia.model.Estudiante;
import com.asistencia.services.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteViewController {

    private final EstudianteService estudianteService;

    public EstudianteViewController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public String listarEstudiantes(Model model) {
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        model.addAttribute("estudiante", new Estudiante()); // para el form si lo usas con th:object
        return "estudiantes";
    }


//    @PostMapping("/guardar")
//    public String guardar(@Valid Estudiante e, BindingResult result){
//        estudianteService.guardar(e);
//        ra.addFlashAttribute("ok", "Estudiante guardado correctamente");
//        return "redirect:/estudiantes";
//    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Estudiante e,
            BindingResult result,
            RedirectAttributes ra,
            Model model
    ) {

        // 🔎 si hay errores de validación backend
        if (result.hasErrors()) {
            model.addAttribute("estudiantes", estudianteService.listarTodos());
            return "estudiantes";
        }

        estudianteService.guardar(e);

        // ✅ mensaje flash bootstrap
        ra.addFlashAttribute("ok", "Estudiante guardado correctamente");

        return "redirect:/estudiantes";
    }
}
