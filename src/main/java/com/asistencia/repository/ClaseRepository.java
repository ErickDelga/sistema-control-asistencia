package com.asistencia.repository;

import com.asistencia.model.Clase;
import com.asistencia.model.Anio;
import com.asistencia.model.TipoBachillerato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {

    List<Clase> findByAnioAndTipoBachilleratoAndSeccion(
            Anio anio,
            TipoBachillerato tipoBachillerato,
            String seccion
    );

}