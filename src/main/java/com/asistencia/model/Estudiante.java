package com.asistencia.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Entity
@Data
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    private Anio anio;

    @Enumerated(EnumType.STRING)
    private TipoBachillerato tipoBachillerato;

    @Column(length = 1)
    private String seccion;

    @Column(name = "foto")
    private String foto;
}