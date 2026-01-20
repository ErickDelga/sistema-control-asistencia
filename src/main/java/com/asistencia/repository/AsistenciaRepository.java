package com.asistencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asistencia.model.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {}
