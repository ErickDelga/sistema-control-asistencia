package com.asistencia.controller.api;

import com.asistencia.dto.AsistenciaRequest;
import com.asistencia.model.Asistencia;
import com.asistencia.services.AsistenciaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService service;

    public AsistenciaController(AsistenciaService service) {
        this.service = service;
    }

    @PostMapping
    public Asistencia registrar(@RequestBody AsistenciaRequest request) {
        return service.registrar(
                request.getEstudianteId(),
                request.getEstado()
        );
    }

    @GetMapping
    public List<Asistencia> listar() {
        return service.listar();
    }

    @GetMapping("/fecha/{fecha}")
    public List<Asistencia> porFecha(@PathVariable String fecha) {
        return service.listarPorFecha(LocalDate.parse(fecha));
    }

    @GetMapping("/estudiante/{id}")
    public List<Asistencia> porEstudiante(@PathVariable Long id) {
        return service.listarPorEstudiante(id);
    }

    @GetMapping("/grado/{grado}")
    public List<Asistencia> porGrado(@PathVariable String grupo) {
        return service.listarPorGrado(grupo);
    }
}
