package com.asistencia.controller.view;

import com.asistencia.model.EstadoAsistencia;
import com.asistencia.services.AsistenciaService;
import com.asistencia.services.EstudianteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("estados", EstadoAsistencia.values());
        return "asistencias";
    }

//    @PostMapping("/guardar")
//    public String guardar(Long estudianteId, EstadoAsistencia estado){
//        asistenciaService.registrar(estudianteId, estado);
//        return "redirect:/asistencias";
//    }
    // ✅ GUARDAR
    @PostMapping("/guardar")
    public String guardar(@RequestParam Long estudianteId,
                          @RequestParam EstadoAsistencia estado,
                          RedirectAttributes ra) {

        try {
            asistenciaService.registrar(estudianteId, estado);
            ra.addFlashAttribute("ok", "Asistencia registrada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al registrar asistencia");
        }

        return "redirect:/asistencias";
    }

}
