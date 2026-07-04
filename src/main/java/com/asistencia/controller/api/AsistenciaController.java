package com.asistencia.controller.api;

import com.asistencia.dto.AsistenciaRequest;
import com.asistencia.model.Anio;
import com.asistencia.model.Asistencia;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.services.AsistenciaService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @PostMapping
    public Asistencia registrar(@RequestBody AsistenciaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede venir vacía");
        }

        if (request.getClaseId() != null) {
            return service.registrarPorClase(
                    request.getEstudianteId(),
                    request.getClaseId(),
                    request.getEstado()
            );
        }

        return service.registrar(
                request.getEstudianteId(),
                request.getEstado()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping
    public List<Asistencia> listar() {
        return service.listar();
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/fecha/{fecha}")
    public List<Asistencia> porFecha(@PathVariable String fecha) {
        return service.listarPorFecha(LocalDate.parse(fecha));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/estudiante/{id}")
    public List<Asistencia> porEstudiante(@PathVariable Long id) {
        return service.listarPorEstudiante(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/anio/{anio}")
    public List<Asistencia> porAnio(@PathVariable Anio anio) {
        return service.listarPorAnio(anio);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/contador")
    public long contarPorFechaYEstado(
            @RequestParam String fecha,
            @RequestParam EstadoAsistencia estado) {

        return service.contarPorFechaYEstado(
                LocalDate.parse(fecha),
                estado
        );
    }
}