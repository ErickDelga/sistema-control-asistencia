package com.asistencia.services;

import com.asistencia.model.Estudiante;
import com.asistencia.repository.EstudianteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudianteService {

    private final EstudianteRepository repo;

    public EstudianteService(EstudianteRepository repo) {
        this.repo = repo;
    }

    public Estudiante guardar(Estudiante e) {
        return repo.save(e);
    }

    public List<Estudiante> listarTodos() {
        return repo.findAll();
    }

    public Estudiante buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
