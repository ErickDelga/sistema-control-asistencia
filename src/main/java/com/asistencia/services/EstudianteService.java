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

    public List<Estudiante> listar() {
        return repo.findAll();
    }
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("El estudiante no existe");
        }
        repo.deleteById(id);
    }
}
