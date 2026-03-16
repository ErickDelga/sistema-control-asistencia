package com.asistencia.services;

import com.asistencia.model.Anio;
import com.asistencia.model.Asistencia;
import com.asistencia.model.Clase;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.model.Estudiante;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.repository.AsistenciaRepository;
import com.asistencia.repository.ClaseRepository;
import com.asistencia.repository.EstudianteRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

@Service
public class AsistenciaService {

    private final AsistenciaRepository repoAsistencia;
    private final EstudianteRepository repoEstudiante;
    private final ClaseRepository repoClase;

    public AsistenciaService(AsistenciaRepository repoAsistencia,
                             EstudianteRepository repoEstudiante,
                             ClaseRepository repoClase) {
        this.repoAsistencia = repoAsistencia;
        this.repoEstudiante = repoEstudiante;
        this.repoClase = repoClase;
    }

    public Asistencia registrar(Long estudianteId, EstadoAsistencia estado) {

        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }

        if (estado == null) {
            throw new IllegalArgumentException("El estado de asistencia es obligatorio");
        }

        LocalDate hoy = LocalDate.now();

        if (repoAsistencia.existsByEstudianteIdAndFecha(estudianteId, hoy)) {
            throw new IllegalArgumentException("El estudiante ya tiene asistencia registrada hoy");
        }

        Estudiante estudiante = repoEstudiante.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        Asistencia asistencia = new Asistencia();
        asistencia.setEstudiante(estudiante);
        asistencia.setEstado(estado);
        asistencia.setFechaHora(LocalDateTime.now());

        return repoAsistencia.save(asistencia);
    }

    public List<Asistencia> listar() {
        return repoAsistencia.findAll();
    }

    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        return repoAsistencia.findByFecha(fecha);
    }

    public List<Asistencia> listarHoy() {
        return repoAsistencia.findByFecha(LocalDate.now());
    }

    public List<Asistencia> listarPorEstudiante(Long estudianteId) {
        return repoAsistencia.findByEstudianteId(estudianteId);
    }

    public List<Asistencia> listarPorAnio(Anio anio) {
        return repoAsistencia.findByEstudianteAnio(anio);
    }

    public long contarPorFechaYEstado(LocalDate fecha, EstadoAsistencia estado) {
        return repoAsistencia.countByFechaAndEstado(fecha, estado);
    }

    public List<Object[]> obtenerResumenSemanal() {
        LocalDate hoy = LocalDate.now();

        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(6);

        return repoAsistencia.resumenSemanalCompleto(inicioSemana, finSemana);
    }

    public List<Object[]> obtenerResumenMensualPorAnio() {
        YearMonth mesActual = YearMonth.now();

        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();

        return repoAsistencia.resumenMensualPorAnioCompleto(inicioMes, finMes);
    }

    public Asistencia registrarPorClase(Long estudianteId,
                                        Long claseId,
                                        EstadoAsistencia estado) {

        if (estudianteId == null) {
            throw new IllegalArgumentException("El estudiante es obligatorio");
        }

        if (claseId == null) {
            throw new IllegalArgumentException("La clase es obligatoria");
        }

        if (estado == null) {
            throw new IllegalArgumentException("El estado de asistencia es obligatorio");
        }

        LocalDate hoy = LocalDate.now();

        if (repoAsistencia.existsByEstudianteIdAndClaseIdAndFecha(estudianteId, claseId, hoy)) {
            throw new IllegalArgumentException(
                    "Este estudiante ya tiene asistencia registrada hoy para esta clase.");
        }

        Estudiante estudiante = repoEstudiante.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        Clase clase = repoClase.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        Asistencia asistencia = new Asistencia();
        asistencia.setEstudiante(estudiante);
        asistencia.setClase(clase);
        asistencia.setEstado(estado);
        asistencia.setFechaHora(LocalDateTime.now());

        return repoAsistencia.save(asistencia);
    }

    public List<Asistencia> buscarParaReporte(LocalDate fecha,
                                              String asignatura,
                                              Anio anio,
                                              TipoBachillerato tipoBachillerato,
                                              String seccion) {

        String asignaturaLimpia = asignatura != null ? asignatura.trim() : null;
        String seccionLimpia = seccion != null ? seccion.trim().toUpperCase(Locale.ROOT) : null;

        return repoAsistencia.buscarParaReporte(
                fecha,
                asignaturaLimpia,
                anio,
                tipoBachillerato,
                seccionLimpia
        );
    }
}