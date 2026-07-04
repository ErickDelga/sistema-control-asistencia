package com.asistencia.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "anio_asignado")
    private Anio anioAsignado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_bachillerato_asignado")
    private TipoBachillerato tipoBachilleratoAsignado;

    @Column(name = "seccion_asignada", length = 1)
    private String seccionAsignada;
}