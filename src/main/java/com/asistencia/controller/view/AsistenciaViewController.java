package com.asistencia.controller.view;

import com.asistencia.services.AsistenciaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AsistenciaViewController {

    private final AsistenciaService asistenciaService;

    public AsistenciaViewController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/asistencias")
    public String listarAsistencias(Model model) {
        model.addAttribute("asistencias", asistenciaService.listarTodas());
        return "asistencias";
    }

}
