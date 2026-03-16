package com.asistencia.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la asignatura
    @Column(nullable = false)
    private String asignatura;

    // Docente que creó la clase
    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Usuario docente;

    // Tipo de bachillerato
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBachillerato tipoBachillerato;

    // Año (1ro,2do,3ro)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Anio anio;

    // Sección
    @Column(nullable = false, length = 1)
    private String seccion;

    // Fecha de creación de la clase
    private LocalDateTime fechaCreacion;

}