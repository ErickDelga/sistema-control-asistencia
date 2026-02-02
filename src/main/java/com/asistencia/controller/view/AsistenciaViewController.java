package com.asistencia.controller.view;

import com.asistencia.model.EstadoAsistencia;
import com.asistencia.services.AsistenciaService;
import com.asistencia.services.EstudianteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaViewController {

    private final AsistenciaService asistenciaService;
    private final EstudianteService estudianteService;

    public AsistenciaViewController(AsistenciaService asistenciaService,
                                    EstudianteService estudianteService) {
        this.asistenciaService = asistenciaService;
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public String listarAsistencias(Model model) {
        model.addAttribute("asistencias", asistenciaService.listarTodas());
        model.addAttribute("estudiantes", estudianteService.listar());
        return "asistencias";
    }

    @PostMapping("/guardar")
    public String guardar(Long estudianteId, EstadoAsistencia estado){
        asistenciaService.registrar(estudianteId, estado);
        return "redirect:/asistencias";
    }

}
