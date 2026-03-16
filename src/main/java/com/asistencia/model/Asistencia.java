package com.asistencia.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Estado de asistencia
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsistencia estado;

    // Relación con estudiante
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    // Relación con clase (NUEVO - pero opcional para no romper tu sistema actual)
    @ManyToOne
    @JoinColumn(name = "clase_id")
    private Clase clase;

    // Fecha y hora de registro
    @Column(nullable = false)
    private LocalDateTime fechaHora;

}