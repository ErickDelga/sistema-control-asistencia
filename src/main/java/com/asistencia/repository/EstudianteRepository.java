package com.asistencia.repository;

import com.asistencia.model.Anio;
import com.asistencia.model.Estudiante;
import com.asistencia.model.TipoBachillerato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findByAnioAndTipoBachilleratoAndSeccion(
            Anio anio,
            TipoBachillerato tipoBachillerato,
            String seccion
    );

    List<Estudiante> findByAnioAndTipoBachilleratoAndSeccionAndActivo(
            Anio anio,
            TipoBachillerato tipoBachillerato,
            String seccion,
            Boolean activo
    );

    List<Estudiante> findByNombresContainingIgnoreCase(String nombres);

    List<Estudiante> findByApellidosContainingIgnoreCase(String apellidos);

    List<Estudiante> findByNombresContainingIgnoreCaseAndApellidosContainingIgnoreCase(
            String nombres,
            String apellidos
    );

    Optional<Estudiante> findFirstByNombresIgnoreCaseAndApellidosIgnoreCaseAndAnioAndTipoBachilleratoAndSeccionIgnoreCase(
            String nombres,
            String apellidos,
            Anio anio,
            TipoBachillerato tipoBachillerato,
            String seccion
    );
}