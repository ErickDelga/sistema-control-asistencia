package com.asistencia.services;

import com.asistencia.model.Anio;
import com.asistencia.model.Estudiante;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.model.Usuario;
import com.asistencia.repository.EstudianteRepository;
import com.asistencia.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class EstudianteService {

    private final EstudianteRepository repo;
    private final UsuarioRepository usuarioRepository;

    public EstudianteService(EstudianteRepository repo,
                             UsuarioRepository usuarioRepository) {
        this.repo = repo;
        this.usuarioRepository = usuarioRepository;
    }

    public Estudiante guardar(Estudiante estudiante) {
        Usuario usuarioActual = obtenerUsuarioActual();

        normalizarEstudiante(estudiante);

        if (esDocente(usuarioActual)) {
            validarResponsabilidadDocente(usuarioActual);

            estudiante.setAnio(usuarioActual.getAnioAsignado());
            estudiante.setTipoBachillerato(usuarioActual.getTipoBachilleratoAsignado());
            estudiante.setSeccion(usuarioActual.getSeccionAsignada());
        }

        if (estudiante.getActivo() == null) {
            estudiante.setActivo(true);
        }

        if (estudiante.getExcluidoInactivacionAutomatica() == null) {
            estudiante.setExcluidoInactivacionAutomatica(false);
        }

        return repo.save(estudiante);
    }

    public List<Estudiante> listarTodos() {
        actualizarEstadosAutomaticos();

        Usuario usuarioActual = obtenerUsuarioActual();

        if (usuarioActual != null && esDocente(usuarioActual)) {
            validarResponsabilidadDocente(usuarioActual);
            return repo.findByAnioAndTipoBachilleratoAndSeccion(
                    usuarioActual.getAnioAsignado(),
                    usuarioActual.getTipoBachilleratoAsignado(),
                    usuarioActual.getSeccionAsignada()
            );
        }

        return repo.findAll();
    }

    public Estudiante buscarPorId(Long id) {
        actualizarEstadosAutomaticos();

        Estudiante estudiante = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Usuario usuarioActual = obtenerUsuarioActual();

        if (usuarioActual != null && esDocente(usuarioActual)) {
            validarResponsabilidadDocente(usuarioActual);

            boolean permitido =
                    estudiante.getAnio() == usuarioActual.getAnioAsignado()
                            && estudiante.getTipoBachillerato() == usuarioActual.getTipoBachilleratoAsignado()
                            && estudiante.getSeccion() != null
                            && estudiante.getSeccion().equalsIgnoreCase(usuarioActual.getSeccionAsignada());

            if (!permitido) {
                throw new RuntimeException("No tienes permisos para acceder a este estudiante");
            }
        }

        return estudiante;
    }

    public void eliminar(Long id) {
        Estudiante estudiante = buscarPorId(id);
        repo.deleteById(estudiante.getId());
    }

    public List<Estudiante> buscarPorClase(
            Anio anio,
            TipoBachillerato tipo,
            String seccion) {

        actualizarEstadosAutomaticos();

        Usuario usuarioActual = obtenerUsuarioActual();

        if (usuarioActual != null && esDocente(usuarioActual)) {
            validarResponsabilidadDocente(usuarioActual);

            if (anio != usuarioActual.getAnioAsignado()
                    || tipo != usuarioActual.getTipoBachilleratoAsignado()
                    || seccion == null
                    || !seccion.equalsIgnoreCase(usuarioActual.getSeccionAsignada())) {
                throw new RuntimeException("No tienes permisos para consultar estudiantes de otra sección");
            }
        }

        return repo.findByAnioAndTipoBachilleratoAndSeccion(
                anio,
                tipo,
                seccion
        );
    }

    public Estudiante activarManual(Long id, String motivo) {
        Estudiante estudiante = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.setActivo(true);
        estudiante.setExcluidoInactivacionAutomatica(true);
        estudiante.setFechaCambioEstado(LocalDate.now());
        estudiante.setMotivoCambioEstado(
                (motivo != null && !motivo.isBlank())
                        ? motivo.trim()
                        : "Reactivado manualmente"
        );

        return repo.save(estudiante);
    }

    public Estudiante desactivarManual(Long id, String motivo) {
        Estudiante estudiante = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.setActivo(false);
        estudiante.setExcluidoInactivacionAutomatica(true);
        estudiante.setFechaCambioEstado(LocalDate.now());
        estudiante.setMotivoCambioEstado(
                (motivo != null && !motivo.isBlank())
                        ? motivo.trim()
                        : "Desactivado manualmente"
        );

        return repo.save(estudiante);
    }

    public void actualizarEstadosAutomaticos() {
        LocalDate hoy = LocalDate.now();
        MonthDay fechaCorte = MonthDay.of(12, 25);

        if (MonthDay.from(hoy).isBefore(fechaCorte)) {
            return;
        }

        List<Estudiante> estudiantes = repo.findAll();

        for (Estudiante estudiante : estudiantes) {
            if (Boolean.TRUE.equals(estudiante.getExcluidoInactivacionAutomatica())) {
                continue;
            }

            if (debeInactivarseAutomaticamente(estudiante)) {
                if (Boolean.TRUE.equals(estudiante.getActivo())) {
                    estudiante.setActivo(false);
                    estudiante.setFechaCambioEstado(hoy);
                    estudiante.setMotivoCambioEstado("Inactivación automática por cierre académico");
                    repo.save(estudiante);
                }
            }
        }
    }

    public int importarDesdeCsv(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String nombre = archivo.getOriginalFilename();
        if (nombre == null || !nombre.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("El archivo debe ser CSV");
        }

        Usuario usuarioActual = obtenerUsuarioActual();
        List<Estudiante> estudiantesAGuardar = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            boolean primeraLinea = true;
            String separador = ",";

            while ((linea = reader.readLine()) != null) {
                if (linea == null || linea.isBlank()) {
                    continue;
                }

                if (primeraLinea) {
                    separador = detectarSeparador(linea);
                }

                String[] partes = linea.split(separador, -1);

                if (primeraLinea) {
                    primeraLinea = false;
                    String encabezado = normalizarTextoPlano(linea);

                    if (encabezado.contains("NOMBRECOMPLETO")
                            || encabezado.contains("NOMBRES")
                            || encabezado.contains("APELLIDOS")
                            || encabezado.contains("ANIO")
                            || encabezado.contains("TIPOBACHILLERATO")) {
                        continue;
                    }
                }

                Estudiante estudiante = new Estudiante();

                if (partes.length == 4) {
                    String nombreCompleto = partes[0].trim();
                    separarNombre(nombreCompleto, estudiante);
                    asignarDatosAcademicos(estudiante, partes[1], partes[2], partes[3], usuarioActual);
                } else if (partes.length >= 5) {
                    estudiante.setNombres(partes[0].trim());
                    estudiante.setApellidos(partes[1].trim());
                    asignarDatosAcademicos(estudiante, partes[2], partes[3], partes[4], usuarioActual);
                } else {
                    throw new IllegalArgumentException("Formato inválido en CSV");
                }

                estudiante.setActivo(true);
                estudiante.setExcluidoInactivacionAutomatica(false);

                normalizarEstudiante(estudiante);

                Optional<Estudiante> existente = repo
                        .findFirstByNombresIgnoreCaseAndApellidosIgnoreCaseAndAnioAndTipoBachilleratoAndSeccionIgnoreCase(
                                estudiante.getNombres(),
                                estudiante.getApellidos(),
                                estudiante.getAnio(),
                                estudiante.getTipoBachillerato(),
                                estudiante.getSeccion()
                        );

                if (existente.isPresent()) {
                    Estudiante actual = existente.get();

                    actual.setNombres(estudiante.getNombres());
                    actual.setApellidos(estudiante.getApellidos());
                    actual.setAnio(estudiante.getAnio());
                    actual.setTipoBachillerato(estudiante.getTipoBachillerato());
                    actual.setSeccion(estudiante.getSeccion());

                    if (actual.getActivo() == null) {
                        actual.setActivo(true);
                    }

                    if (actual.getExcluidoInactivacionAutomatica() == null) {
                        actual.setExcluidoInactivacionAutomatica(false);
                    }

                    estudiantesAGuardar.add(actual);
                } else {
                    estudiantesAGuardar.add(estudiante);
                }
            }

            repo.saveAll(estudiantesAGuardar);
            return estudiantesAGuardar.size();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo CSV: " + e.getMessage());
        }
    }

    private boolean debeInactivarseAutomaticamente(Estudiante estudiante) {
        if (estudiante.getTipoBachillerato() == null || estudiante.getAnio() == null) {
            return false;
        }

        if (estudiante.getTipoBachillerato() == TipoBachillerato.BACHILLERATO_GENERAL) {
            return estudiante.getAnio() == Anio.SEGUNDO;
        }

        return estudiante.getAnio() == Anio.TERCERO;
    }

    private void normalizarEstudiante(Estudiante estudiante) {
        if (estudiante.getNombres() == null || estudiante.getNombres().isBlank()) {
            throw new RuntimeException("Los nombres son obligatorios");
        }

        if (estudiante.getApellidos() == null || estudiante.getApellidos().isBlank()) {
            throw new RuntimeException("Los apellidos son obligatorios");
        }

        estudiante.setNombres(estudiante.getNombres().trim().replaceAll("\\s+", " "));
        estudiante.setApellidos(estudiante.getApellidos().trim().replaceAll("\\s+", " "));

        if (estudiante.getSeccion() != null) {
            estudiante.setSeccion(estudiante.getSeccion().trim().toUpperCase());
            if (estudiante.getSeccion().length() > 1) {
                estudiante.setSeccion(estudiante.getSeccion().substring(0, 1));
            }
        }
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return usuarioRepository.findByUsername(auth.getName()).orElse(null);
    }

    private boolean esDocente(Usuario usuario) {
        return usuario != null && usuario.getRol() != null && usuario.getRol().name().equals("DOCENTE");
    }

    private void validarResponsabilidadDocente(Usuario docente) {
        if (docente.getAnioAsignado() == null
                || docente.getTipoBachilleratoAsignado() == null
                || docente.getSeccionAsignada() == null
                || docente.getSeccionAsignada().isBlank()) {
            throw new RuntimeException("El docente no tiene asignada una sección responsable");
        }
    }

    private String detectarSeparador(String linea) {
        if (linea.contains(";")) return ";";
        if (linea.contains("\t")) return "\t";
        return ",";
    }

    private void separarNombre(String nombreCompleto, Estudiante estudiante) {
        String limpio = nombreCompleto == null ? "" : nombreCompleto.trim().replaceAll("\\s+", " ");
        String[] partes = limpio.split("\\s+");

        if (partes.length >= 2) {
            estudiante.setNombres(partes[0]);
            estudiante.setApellidos(String.join(" ", java.util.Arrays.copyOfRange(partes, 1, partes.length)));
        } else {
            estudiante.setNombres(limpio);
            estudiante.setApellidos("N/A");
        }
    }

    private void asignarDatosAcademicos(Estudiante estudiante,
                                        String anio,
                                        String tipo,
                                        String seccion,
                                        Usuario usuarioActual) {
        if (esDocente(usuarioActual)) {
            validarResponsabilidadDocente(usuarioActual);

            estudiante.setAnio(usuarioActual.getAnioAsignado());
            estudiante.setTipoBachillerato(usuarioActual.getTipoBachilleratoAsignado());
            estudiante.setSeccion(usuarioActual.getSeccionAsignada());
            return;
        }

        estudiante.setAnio(convertirAnio(anio));
        estudiante.setTipoBachillerato(convertirTipoBachillerato(tipo));
        estudiante.setSeccion(seccion == null ? null : seccion.trim().toUpperCase());
    }

    private Anio convertirAnio(String valor) {
        String normalizado = normalizarTextoPlano(valor);

        return switch (normalizado) {
            case "PRIMERO", "1RO", "1", "PRIMER" -> Anio.PRIMERO;
            case "SEGUNDO", "2DO", "2" -> Anio.SEGUNDO;
            case "TERCERO", "3RO", "3" -> Anio.TERCERO;
            default -> {
                try {
                    yield Anio.valueOf(valor.trim().toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Año no válido: " + valor);
                }
            }
        };
    }

    private TipoBachillerato convertirTipoBachillerato(String valor) {
        String normalizado = normalizarTextoPlano(valor);

        return switch (normalizado) {
            case "BACHILLERATOGENERAL", "GENERAL" -> TipoBachillerato.BACHILLERATO_GENERAL;

            case "TECNICOPRODUCTIVOENSALUDYBIENESTARSOCIAL",
                 "SALUDYBIENESTARSOCIAL",
                 "TECNICOSALUD",
                 "SALUD" -> TipoBachillerato.TECNICO_PRODUCTIVO_EN_SALUD_Y_BIENESTAR_SOCIAL;

            case "ADMINISTRATIVOCONTABLE",
                 "CONTABLE",
                 "ADMINISTRATIVO" -> TipoBachillerato.ADMINISTRATIVO_CONTABLE;

            case "INFRAESTRUCTURATECNOLOGICAYSERVICIOSINFORMATICOS",
                 "INFORMATICA",
                 "SERVICIOSINFORMATICOS",
                 "INFRAESTRUCTURATECNOLOGICA" -> TipoBachillerato.INFRAESTRUCTURA_TECNOLOGICA_Y_SERVICIOS_INFORMATICOS;

            default -> {
                try {
                    yield TipoBachillerato.valueOf(valor.trim().toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Tipo de bachillerato no válido: " + valor);
                }
            }
        };
    }

    private String normalizarTextoPlano(String texto) {
        if (texto == null) {
            return "";
        }

        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return sinTildes
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]", "");
    }
}