package com.asistencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asistencia.model.Asistencia;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByFecha(LocalDate fecha);

    List<Asistencia> findByEstudianteId(Long estudianteId);

    List<Asistencia> findByEstudianteGrado(String grado);
}
