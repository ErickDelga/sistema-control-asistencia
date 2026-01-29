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

    @PostMapping
    public Estudiante crear(@RequestBody Estudiante e) {
        System.out.println("LLEGO ESTUDIANTE: " + e);
        return service.guardar(e);
    }

    @GetMapping
    public List<Estudiante> listar() {
        return service.listar();
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Estudiante> actualizar(
            @PathVariable Long id,
            @RequestBody Estudiante estudiante) {

        Estudiante actualizado = service.actualizar(id, estudiante);
        return ResponseEntity.ok(actualizado);
    }

}
