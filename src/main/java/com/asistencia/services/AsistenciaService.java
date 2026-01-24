package com.asistencia.services;

import com.asistencia.model.Asistencia;
import com.asistencia.repository.AsistenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsistenciaService {

    private final AsistenciaRepository repo;

    public AsistenciaService(AsistenciaRepository repo){
        this.repo = repo;
    }

    public Asistencia registrar(Asistencia a) {
        if (a.getFecha().isAfter(LocalDate.now())) {
            throw new RuntimeException("La fecha no puede ser futura");
        }
        return repo.save(a);
    }
    public List<Asistencia> listar() {
        return repo.findAll();
    }
    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        return repo.findByFecha(fecha);
    }

    public List<Asistencia> listarPorEstudiante(Long estudianteId) {
        return repo.findByEstudianteId(estudianteId);
    }

    public List<Asistencia> listarPorGrado(String grupo) {
        return repo.findByEstudianteGrado(grupo);
    }
}
