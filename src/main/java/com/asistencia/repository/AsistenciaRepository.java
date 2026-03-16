package com.asistencia.repository;

import com.asistencia.model.Anio;
import com.asistencia.model.Asistencia;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.model.TipoBachillerato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    @Query("""
        SELECT a FROM Asistencia a
        WHERE DATE(a.fechaHora) = :fecha
        ORDER BY a.fechaHora DESC
    """)
    List<Asistencia> findByFecha(@Param("fecha") LocalDate fecha);

    List<Asistencia> findByEstudianteId(Long estudianteId);

    List<Asistencia> findByEstudianteAnio(Anio anio);

    @Query("""
        SELECT COUNT(a) > 0
        FROM Asistencia a
        WHERE a.estudiante.id = :estudianteId
        AND DATE(a.fechaHora) = :fecha
    """)
    boolean existsByEstudianteIdAndFecha(
            @Param("estudianteId") Long estudianteId,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
        SELECT COUNT(a) > 0
        FROM Asistencia a
        WHERE a.estudiante.id = :estudianteId
        AND a.clase.id = :claseId
        AND DATE(a.fechaHora) = :fecha
    """)
    boolean existsByEstudianteIdAndClaseIdAndFecha(
            @Param("estudianteId") Long estudianteId,
            @Param("claseId") Long claseId,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
        SELECT COUNT(a)
        FROM Asistencia a
        WHERE DATE(a.fechaHora) = :fecha
        AND a.estado = :estado
    """)
    long countByFechaAndEstado(
            @Param("fecha") LocalDate fecha,
            @Param("estado") EstadoAsistencia estado
    );

    @Query("""
        SELECT DATE(a.fechaHora), a.estado, COUNT(a)
        FROM Asistencia a
        WHERE DATE(a.fechaHora) BETWEEN :inicio AND :fin
        GROUP BY DATE(a.fechaHora), a.estado
        ORDER BY DATE(a.fechaHora)
    """)
    List<Object[]> resumenSemanalCompleto(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    @Query("""
        SELECT a.estudiante.anio, a.estado, COUNT(a)
        FROM Asistencia a
        WHERE DATE(a.fechaHora) BETWEEN :inicioMes AND :finMes
        GROUP BY a.estudiante.anio, a.estado
    """)
    List<Object[]> resumenMensualPorAnioCompleto(
            @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes
    );

    @Query("""
        SELECT a
        FROM Asistencia a
        LEFT JOIN a.estudiante e
        LEFT JOIN a.clase c
        WHERE (:fecha IS NULL OR DATE(a.fechaHora) = :fecha)
          AND (:asignatura IS NULL OR :asignatura = '' OR LOWER(c.asignatura) LIKE LOWER(CONCAT('%', :asignatura, '%')))
          AND (:anio IS NULL OR e.anio = :anio)
          AND (:tipoBachillerato IS NULL OR e.tipoBachillerato = :tipoBachillerato)
          AND (:seccion IS NULL OR :seccion = '' OR UPPER(e.seccion) = UPPER(:seccion))
        ORDER BY a.fechaHora DESC
    """)
    List<Asistencia> buscarParaReporte(
            @Param("fecha") LocalDate fecha,
            @Param("asignatura") String asignatura,
            @Param("anio") Anio anio,
            @Param("tipoBachillerato") TipoBachillerato tipoBachillerato,
            @Param("seccion") String seccion
    );
}