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
        return "estudiantes";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Estudiante e, BindingResult result){
        estudianteService.guardar(e);
        return "redirect:/estudiantes";
    }

}
