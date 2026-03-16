package com.asistencia.controller.api;

import com.asistencia.model.Estudiante;
import com.asistencia.services.EstudianteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService service;

    public EstudianteController(EstudianteService service) {
        this.service = service;
    }

    // CREAR
    @PostMapping
    public ResponseEntity<Estudiante> crear(@RequestBody Estudiante estudiante) {
        return ResponseEntity.ok(service.guardar(estudiante));
    }

    // LISTAR
    @GetMapping
    public ResponseEntity<List<Estudiante>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // BUSCAR
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Estudiante> actualizar(
            @PathVariable Long id,
            @RequestBody Estudiante datos) {

        Estudiante e = service.buscarPorId(id);

        e.setNombreCompleto(datos.getNombreCompleto());
        e.setAnio(datos.getAnio());
        e.setTipoBachillerato(datos.getTipoBachillerato());
        e.setSeccion(datos.getSeccion());

        return ResponseEntity.ok(service.guardar(e));
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}