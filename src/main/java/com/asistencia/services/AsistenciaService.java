package com.asistencia.services;

import com.asistencia.model.Anio;
import com.asistencia.model.Asistencia;
import com.asistencia.model.Clase;
import com.asistencia.model.EstadoAsistencia;
import com.asistencia.model.Estudiante;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.model.Usuario;
import com.asistencia.repository.AsistenciaRepository;
import com.asistencia.repository.ClaseRepository;
import com.asistencia.repository.EstudianteRepository;
import com.asistencia.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AsistenciaService {

    private final AsistenciaRepository repoAsistencia;
    private final EstudianteRepository repoEstudiante;
    private final ClaseRepository repoClase;
    private final UsuarioRepository usuarioRepository;

    public AsistenciaService(AsistenciaRepository repoAsistencia,
                             EstudianteRepository repoEstudiante,
                             ClaseRepository repoClase,
                             UsuarioRepository usuarioRepository) {
        this.repoAsistencia = repoAsistencia;
        this.repoEstudiante = repoEstudiante;
        this.repoClase = repoClase;
        this.usuarioRepository = usuarioRepository;
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
        return buscarConFiltros(null, null, null, null, null, null, null, null);
    }

    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        return buscarConFiltros(fecha, fecha, null, null, null, null, null, null);
    }

    public List<Asistencia> listarHoy() {
        LocalDate hoy = LocalDate.now();
        return buscarConFiltros(hoy, hoy, null, null, null, null, null, null);
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
        return registrarPorClase(estudianteId, claseId, estado, null, null);
    }

    public Asistencia registrarPorClase(Long estudianteId,
                                        Long claseId,
                                        EstadoAsistencia estado,
                                        String motivo,
                                        String comprobante) {

        if (estudianteId == null) {
            throw new IllegalArgumentException("El estudiante es obligatorio");
        }

        if (claseId == null) {
            throw new IllegalArgumentException("La clase es obligatoria");
        }

        if (estado == null) {
            throw new IllegalArgumentException("El estado de asistencia es obligatorio");
        }

        Usuario usuarioActual = obtenerUsuarioActual();

        Estudiante estudiante = repoEstudiante.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        Clase clase = repoClase.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        validarPermisoSobreClase(clase, usuarioActual);
        validarEstudiantePerteneceAClase(estudiante, clase);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.minusHours(1);

        if (repoAsistencia.existsBloqueoUnaHora(estudianteId, claseId, limite)) {
            long minutos = obtenerMinutosRestantesBloqueo(estudianteId, claseId);
            throw new IllegalArgumentException(
                    "La asistencia ya fue registrada. Podrá volver a registrarse en " + minutos + " minuto(s)."
            );
        }

        Asistencia asistencia = new Asistencia();
        asistencia.setEstudiante(estudiante);
        asistencia.setClase(clase);
        asistencia.setEstado(estado);
        asistencia.setFechaHora(ahora);

        if (motivo != null && !motivo.isBlank()) {
            asistencia.setMotivo(motivo.trim());
        }

        if (comprobante != null && !comprobante.isBlank()) {
            asistencia.setComprobante(comprobante);
        }

        return repoAsistencia.save(asistencia);
    }

    public long obtenerMinutosRestantesBloqueo(Long estudianteId, Long claseId) {
        List<Asistencia> asistencias = repoAsistencia
                .findTopByEstudianteIdAndClaseIdOrderByFechaHoraDesc(estudianteId, claseId);

        if (asistencias.isEmpty()) {
            return 0;
        }

        Asistencia ultima = asistencias.get(0);
        LocalDateTime liberaEn = ultima.getFechaHora().plusHours(1);
        LocalDateTime ahora = LocalDateTime.now();

        if (!liberaEn.isAfter(ahora)) {
            return 0;
        }

        long minutos = Duration.between(ahora, liberaEn).toMinutes();
        return Math.max(minutos, 1);
    }

    public Map<Long, Long> obtenerBloqueosParaClase(Long claseId, List<Estudiante> estudiantes) {
        Map<Long, Long> bloqueos = new HashMap<>();

        for (Estudiante estudiante : estudiantes) {
            long minutos = obtenerMinutosRestantesBloqueo(estudiante.getId(), claseId);
            if (minutos > 0) {
                bloqueos.put(estudiante.getId(), minutos);
            }
        }

        return bloqueos;
    }

    public List<Asistencia> buscarConFiltros(LocalDate fechaInicio,
                                             LocalDate fechaFin,
                                             String nombres,
                                             String apellidos,
                                             String asignatura,
                                             Anio anio,
                                             TipoBachillerato tipoBachillerato,
                                             String seccion) {

        Usuario usuarioActual = obtenerUsuarioActual();

        String nombresLimpios = limpiarTexto(nombres);
        String apellidosLimpios = limpiarTexto(apellidos);
        String asignaturaLimpia = limpiarTexto(asignatura);
        String seccionLimpia = limpiarTexto(seccion);

        if (seccionLimpia != null) {
            seccionLimpia = seccionLimpia.toUpperCase(Locale.ROOT);
        }

        Long docenteId = null;

        if (esDocente(usuarioActual)) {
            docenteId = usuarioActual.getId();
        }

        return repoAsistencia.buscarConFiltros(
                fechaInicio,
                fechaFin,
                nombresLimpios,
                apellidosLimpios,
                asignaturaLimpia,
                anio,
                tipoBachillerato,
                seccionLimpia,
                docenteId
        );
    }

    private void validarPermisoSobreClase(Clase clase, Usuario usuarioActual) {
        if (usuarioActual == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

        if (esAdmin(usuarioActual) || esRectoria(usuarioActual)) {
            return;
        }

        if (esDocente(usuarioActual)) {
            if (clase.getDocente() == null
                    || clase.getDocente().getId() == null
                    || !clase.getDocente().getId().equals(usuarioActual.getId())) {
                throw new IllegalArgumentException("No puedes registrar asistencias en clases creadas por otro docente");
            }
            return;
        }

        throw new IllegalArgumentException("No tienes permisos para registrar asistencias");
    }

    private void validarEstudiantePerteneceAClase(Estudiante estudiante, Clase clase) {
        boolean coincide =
                estudiante.getAnio() == clase.getAnio()
                        && estudiante.getTipoBachillerato() == clase.getTipoBachillerato()
                        && estudiante.getSeccion() != null
                        && clase.getSeccion() != null
                        && estudiante.getSeccion().equalsIgnoreCase(clase.getSeccion());

        if (!coincide) {
            throw new IllegalArgumentException("El estudiante no pertenece a la sección de la clase");
        }
    }

    private String limpiarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim().replaceAll("\\s+", " ");
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return usuarioRepository.findByUsername(auth.getName()).orElse(null);
    }

    private boolean esDocente(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && "DOCENTE".equals(usuario.getRol().name());
    }

    private boolean esAdmin(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && "ADMIN".equals(usuario.getRol().name());
    }

    private boolean esRectoria(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && "RECTORIA".equals(usuario.getRol().name());
    }
}