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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsistencia estado;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "clase_id")
    private Clase clase;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 500)
    private String motivo;

    @Column(name = "comprobante")
    private String comprobante;
}