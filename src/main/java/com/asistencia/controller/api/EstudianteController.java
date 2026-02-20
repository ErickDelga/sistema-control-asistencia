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

    // ✅ CREAR
    @PostMapping
    public ResponseEntity<Estudiante> crear(@RequestBody Estudiante e) {
        Estudiante guardado = service.guardar(e);
        return ResponseEntity.ok(guardado);
    }

    // ✅ LISTAR
    @GetMapping
    public ResponseEntity<List<Estudiante>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // ✅ BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // ✅ ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Estudiante> actualizar(
            @PathVariable Long id,
            @RequestBody Estudiante datos) {

        Estudiante existente = service.buscarPorId(id);

        existente.setNombre(datos.getNombre());
        existente.setGrado(datos.getGrado());

        Estudiante actualizado = service.guardar(existente);

        return ResponseEntity.ok(actualizado);
    }

    // ✅ ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
