package com.asistencia.controller;

import com.asistencia.model.Asistencia;
import com.asistencia.services.AsistenciaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService service;

    public AsistenciaController(AsistenciaService service) {
        this.service = service;
    }

    @PostMapping
    public Asistencia registrar(@RequestBody Asistencia a) {
        return service.registrar(a);
    }
    @GetMapping
    public List<Asistencia> listar() {
        return service.listar();
    }

}
