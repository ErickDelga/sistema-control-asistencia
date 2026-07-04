package com.asistencia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombres;

    @NotBlank
    @Column(nullable = false)
    private String apellidos;

    // Se conserva para no romper lo que ya usa nombreCompleto en vistas y reportes
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    private Anio anio;

    @Enumerated(EnumType.STRING)
    private TipoBachillerato tipoBachillerato;

    @Column(length = 1)
    private String seccion;

    @Column(name = "foto")
    private String foto;

    @Column(nullable = false)
    private Boolean activo = true;

    // Si se reactiva manualmente, no se vuelve a desactivar automáticamente
    @Column(name = "excluido_inactivacion_automatica", nullable = false)
    private Boolean excluidoInactivacionAutomatica = false;

    @Column(name = "fecha_cambio_estado")
    private LocalDate fechaCambioEstado;

    @Column(name = "motivo_cambio_estado", length = 300)
    private String motivoCambioEstado;

    @PrePersist
    @PreUpdate
    public void sincronizarNombreCompleto() {
        String nombresLimpios = nombres != null ? nombres.trim().replaceAll("\\s+", " ") : "";
        String apellidosLimpios = apellidos != null ? apellidos.trim().replaceAll("\\s+", " ") : "";
        this.nombreCompleto = (nombresLimpios + " " + apellidosLimpios).trim();

        if (seccion != null) {
            seccion = seccion.trim().toUpperCase();
            if (seccion.length() > 1) {
                seccion = seccion.substring(0, 1);
            }
        }

        if (activo == null) {
            activo = true;
        }

        if (excluidoInactivacionAutomatica == null) {
            excluidoInactivacionAutomatica = false;
        }
    }
}