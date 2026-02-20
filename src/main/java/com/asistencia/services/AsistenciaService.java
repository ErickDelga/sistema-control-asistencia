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

    // ===============================
    // REGISTRAR ASISTENCIA (CON VALIDACIÓN)
    // ===============================
    public Asistencia registrar(Long estudianteId, EstadoAsistencia estado){

        LocalDate hoy = LocalDate.now();

        // 🔴 Validar que no exista asistencia duplicada
        if (repoAsistencia.existsByEstudianteIdAndFecha(estudianteId, hoy)) {
            throw new RuntimeException("El estudiante ya tiene asistencia registrada hoy");
        }

        Estudiante estudiante = repoEstudiante.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Asistencia asistencia = new Asistencia();
        asistencia.setFecha(hoy);
        asistencia.setEstado(estado);
        asistencia.setEstudiante(estudiante);

        return repoAsistencia.save(asistencia);
    }

    // ===============================
    // LISTAR TODAS
    // ===============================
    public List<Asistencia> listar() {
        return repoAsistencia.findAll();
    }

    // ===============================
    // LISTAR POR FECHA
    // ===============================
    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        return repoAsistencia.findByFecha(fecha);
    }

    // ===============================
    // LISTAR POR ESTUDIANTE
    // ===============================
    public List<Asistencia> listarPorEstudiante(Long estudianteId) {
        return repoAsistencia.findByEstudianteId(estudianteId);
    }

    // ===============================
    // LISTAR POR GRADO
    // ===============================
    public List<Asistencia> listarPorGrado(String grado) {
        return repoAsistencia.findByEstudianteGrado(grado);
    }
}
