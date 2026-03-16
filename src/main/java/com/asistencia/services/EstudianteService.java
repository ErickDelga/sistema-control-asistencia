package com.asistencia.services;

import com.asistencia.model.Anio;
import com.asistencia.model.Estudiante;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class EstudianteService {

    private final EstudianteRepository repo;

    public EstudianteService(EstudianteRepository repo) {
        this.repo = repo;
    }

    public Estudiante guardar(Estudiante estudiante) {
        normalizarEstudiante(estudiante);
        return repo.save(estudiante);
    }

    public List<Estudiante> listarTodos() {
        return repo.findAll();
    }

    public Estudiante buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public List<Estudiante> buscarPorClase(
            Anio anio,
            TipoBachillerato tipo,
            String seccion) {

        return repo.findByAnioAndTipoBachilleratoAndSeccion(
                anio,
                tipo,
                seccion
        );
    }

    public int importarDesdeCsv(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo CSV.");
        }

        String nombre = archivo.getOriginalFilename();
        if (nombre == null || !nombre.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new IllegalArgumentException("El archivo debe tener extensión .csv");
        }

        try {
            byte[] contenido = archivo.getBytes();

            List<Estudiante> estudiantes = intentarLeerCsv(contenido, StandardCharsets.UTF_8);

            if (estudiantes.isEmpty()) {
                estudiantes = intentarLeerCsv(contenido, Charset.forName("Windows-1252"));
            }

            if (estudiantes.isEmpty()) {
                throw new IllegalArgumentException("El archivo no contiene estudiantes válidos.");
            }

            repo.saveAll(estudiantes);
            return estudiantes.size();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo leer el archivo CSV.");
        }
    }

    private List<Estudiante> intentarLeerCsv(byte[] contenido, Charset charset) {
        List<Estudiante> estudiantes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(contenido), charset))) {

            String linea;
            int numeroLinea = 0;

            while ((linea = br.readLine()) != null) {
                numeroLinea++;

                linea = limpiarBOM(linea).trim();

                if (linea.isBlank()) {
                    continue;
                }

                String[] partes = dividirLineaCsv(linea);

                if (esEncabezado(partes)) {
                    continue;
                }

                if (partes.length < 4) {
                    throw new IllegalArgumentException(
                            "La línea " + numeroLinea + " no tiene el formato esperado. " +
                                    "Debe incluir: nombreCompleto, anio, tipoBachillerato, seccion"
                    );
                }

                Estudiante estudiante = new Estudiante();
                estudiante.setNombreCompleto(partes[0].trim());
                estudiante.setAnio(convertirAnio(partes[1].trim()));
                estudiante.setTipoBachillerato(convertirTipoBachillerato(partes[2].trim()));
                estudiante.setSeccion(normalizarSeccion(partes[3].trim()));

                validarEstudiante(estudiante, numeroLinea);
                normalizarEstudiante(estudiante);

                estudiantes.add(estudiante);
            }

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return new ArrayList<>();
        }

        return estudiantes;
    }

    private void validarEstudiante(Estudiante estudiante, int numeroLinea) {
        if (estudiante.getNombreCompleto() == null || estudiante.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("La línea " + numeroLinea + " no tiene nombre completo válido.");
        }

        if (estudiante.getAnio() == null) {
            throw new IllegalArgumentException("La línea " + numeroLinea + " no tiene un año válido.");
        }

        if (estudiante.getTipoBachillerato() == null) {
            throw new IllegalArgumentException("La línea " + numeroLinea + " no tiene un tipo de bachillerato válido.");
        }

        if (estudiante.getSeccion() == null || estudiante.getSeccion().isBlank()) {
            throw new IllegalArgumentException("La línea " + numeroLinea + " no tiene sección válida.");
        }
    }

    private void normalizarEstudiante(Estudiante estudiante) {
        if (estudiante.getNombreCompleto() != null) {
            estudiante.setNombreCompleto(estudiante.getNombreCompleto().trim().replaceAll("\\s+", " "));
        }

        if (estudiante.getSeccion() != null) {
            estudiante.setSeccion(normalizarSeccion(estudiante.getSeccion()));
        }
    }

    private String[] dividirLineaCsv(String linea) {
        String separador = linea.contains(";") ? ";" : ",";
        String[] partes = linea.split(separador);

        for (int i = 0; i < partes.length; i++) {
            partes[i] = partes[i].trim();
            if (partes[i].startsWith("\"") && partes[i].endsWith("\"") && partes[i].length() >= 2) {
                partes[i] = partes[i].substring(1, partes[i].length() - 1).trim();
            }
        }

        return partes;
    }

    private boolean esEncabezado(String[] partes) {
        if (partes.length == 0) {
            return false;
        }

        String primera = normalizarTexto(partes[0]);

        return primera.equals("nombre")
                || primera.equals("nombrecompleto")
                || primera.equals("estudiante")
                || primera.equals("nombre_del_estudiante");
    }

    private Anio convertirAnio(String valor) {
        String v = normalizarTexto(valor);

        if (v.equals("1") || v.equals("1ro") || v.equals("primero") || v.equals("primer") || v.equals("primeroanio")) {
            return Anio.PRIMERO;
        }

        if (v.equals("2") || v.equals("2do") || v.equals("segundo") || v.equals("segundoanio")) {
            return Anio.SEGUNDO;
        }

        if (v.equals("3") || v.equals("3ro") || v.equals("tercero") || v.equals("tercer") || v.equals("terceroanio")) {
            return Anio.TERCERO;
        }

        try {
            return Anio.valueOf(v.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new IllegalArgumentException("Año no válido: " + valor);
        }
    }

    private TipoBachillerato convertirTipoBachillerato(String valor) {
        String v = normalizarTexto(valor);

        if (v.equals("general")) {
            return TipoBachillerato.GENERAL;
        }

        if (v.equals("tecnico") || v.equals("tecnica") || v.equals("tecnicoindustrial")) {
            return TipoBachillerato.TECNICO;
        }

        try {
            return TipoBachillerato.valueOf(v.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de bachillerato no válido: " + valor);
        }
    }

    private String normalizarSeccion(String seccion) {
        if (seccion == null) {
            return null;
        }

        String limpia = seccion.trim().toUpperCase(Locale.ROOT);
        return limpia.isEmpty() ? limpia : limpia.substring(0, 1);
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return sinTildes
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }

    private String limpiarBOM(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        if (texto.charAt(0) == '\uFEFF') {
            return texto.substring(1);
        }

        return texto;
    }
}