package com.asistencia.services;

import com.asistencia.dto.AsistenciaRequest;
import com.asistencia.model.Asistencia;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.model.Estudiante;
import com.asistencia.repository.AsistenciaRepository;
import com.asistencia.repository.EstudianteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsistenciaService {

    private final AsistenciaRepository repoAsistencia;
    private final EstudianteRepository repoEstudiante;

    public AsistenciaService(AsistenciaRepository repoAsistencia,
                             EstudianteRepository repoEstudiante){
        this.repoAsistencia = repoAsistencia;
        this.repoEstudiante = repoEstudiante;
    }

//    public Asistencia registrar(Asistencia a) {
//        if (a.getFecha().isAfter(LocalDate.now())) {
//            throw new RuntimeException("La fecha no puede ser futura");
//        }
//        return repo.save(a);
//    }

    public Asistencia registrar(AsistenciaRequest request) {

        Estudiante estudiante = repoEstudiante.findById(request.getEstudianteId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Asistencia asistencia = new Asistencia();
        asistencia.setFecha(LocalDate.now());
        asistencia.setEstado(request.getEstado());
        asistencia.setEstudiante(estudiante);

        return repoAsistencia.save(asistencia);
    }

    public List<Asistencia> listar() {
        return repoAsistencia.findAll();
    }
    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        return repoAsistencia.findByFecha(fecha);
    }

    public List<Asistencia> listarPorEstudiante(Long estudianteId) {
        return repoAsistencia.findByEstudianteId(estudianteId);
    }

    public List<Asistencia> listarPorGrado(String grupo) {
        return repoAsistencia.findByEstudianteGrado(grupo);
    }
    public List<Asistencia> listarTodas() {
        return repoAsistencia.findAll();
    }
    public Asistencia registrar(Long estudianteId, EstadoAsistencia estado){

        Estudiante estudiante = repoEstudiante.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Asistencia a = new Asistencia();
        a.setFecha(LocalDate.now());
        a.setEstado(estado);
        a.setEstudiante(estudiante);

        return repoAsistencia.save(a);
    }
}
