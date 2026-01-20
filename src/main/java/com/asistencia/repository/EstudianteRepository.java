package com.asistencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asistencia.model.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {}
