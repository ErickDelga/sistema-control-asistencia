package com.asistencia.controller.api;

import com.asistencia.model.Clase;
import com.asistencia.services.ClaseService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseService service;

    public ClaseController(ClaseService service) {
        this.service = service;
    }

    // ========================
    // CREAR CLASE
    // ========================
    @PostMapping
    public Clase crear(@RequestBody Clase clase) {
        return service.crearClase(clase);
    }

    // ========================
    // LISTAR CLASES
    // ========================
    @GetMapping
    public List<Clase> listar() {
        return service.listar();
    }

}