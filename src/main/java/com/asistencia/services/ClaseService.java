package com.asistencia.services;

import com.asistencia.model.Clase;
import com.asistencia.model.Rol;
import com.asistencia.model.Usuario;
import com.asistencia.repository.ClaseRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClaseService {

    private final ClaseRepository repo;
    private final UsuarioService usuarioService;

    public ClaseService(ClaseRepository repo,
                        UsuarioService usuarioService) {
        this.repo = repo;
        this.usuarioService = usuarioService;
    }

    public Clase crearClase(Clase clase) {
        Authentication auth = obtenerAutenticacionValida();
        Usuario usuarioActual = usuarioService.buscarPorUsername(auth.getName());

        boolean esAdminORectoria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_RECTORIA"));

        if (esAdminORectoria) {
            if (clase.getDocente() == null || clase.getDocente().getId() == null) {
                throw new RuntimeException("Debes seleccionar un docente");
            }

            Usuario docente = usuarioService.buscarPorId(clase.getDocente().getId());

            if (docente == null || docente.getRol() != Rol.DOCENTE) {
                throw new RuntimeException("El usuario seleccionado no es un docente válido");
            }

            clase.setDocente(docente);
        } else {
            clase.setDocente(usuarioActual);
        }

        normalizarClase(clase);
        clase.setFechaCreacion(LocalDateTime.now());

        return repo.save(clase);
    }

    public Clase actualizarClase(Clase datos) {
        Authentication auth = obtenerAutenticacionValida();

        boolean esAdminORectoria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_RECTORIA"));

        if (!esAdminORectoria) {
            throw new RuntimeException("No tienes permisos para editar clases");
        }

        Clase clase = repo.findById(datos.getId())
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        clase.setAsignatura(datos.getAsignatura());
        clase.setAnio(datos.getAnio());
        clase.setTipoBachillerato(datos.getTipoBachillerato());
        clase.setSeccion(datos.getSeccion());

        if (datos.getDocente() == null || datos.getDocente().getId() == null) {
            throw new RuntimeException("Debes seleccionar un docente");
        }

        Usuario docente = usuarioService.buscarPorId(datos.getDocente().getId());

        if (docente == null || docente.getRol() != Rol.DOCENTE) {
            throw new RuntimeException("El usuario seleccionado no es un docente válido");
        }

        clase.setDocente(docente);

        normalizarClase(clase);

        return repo.save(clase);
    }

    public void eliminarClase(Long id) {
        repo.deleteById(id);
    }

    public List<Clase> listar() {
        return repo.findAll();
    }

    public List<Clase> listarParaUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Collections.emptyList();
        }

        String username = auth.getName();

        boolean veTodas = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(role ->
                        role.equals("ROLE_ADMIN")
                                || role.equals("ROLE_RECTORIA")
                                || role.equals("ROLE_SECRETARIA"));

        if (veTodas) {
            return repo.findAll();
        }

        return repo.findAll().stream()
                .filter(clase -> clase.getDocente() != null)
                .filter(clase -> Objects.equals(clase.getDocente().getUsername(), username))
                .collect(Collectors.toList());
    }

    public Clase buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
    }

    private Authentication obtenerAutenticacionValida() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return auth;
    }

    private void normalizarClase(Clase clase) {
        if (clase.getAsignatura() == null || clase.getAsignatura().isBlank()) {
            throw new RuntimeException("La asignatura es obligatoria");
        }

        if (clase.getAnio() == null) {
            throw new RuntimeException("El año es obligatorio");
        }

        if (clase.getTipoBachillerato() == null) {
            throw new RuntimeException("El tipo de bachillerato es obligatorio");
        }

        if (clase.getSeccion() == null || clase.getSeccion().isBlank()) {
            throw new RuntimeException("La sección es obligatoria");
        }

        clase.setAsignatura(clase.getAsignatura().trim().replaceAll("\\s+", " "));
        clase.setSeccion(clase.getSeccion().trim().toUpperCase());

        if (clase.getSeccion().length() > 1) {
            clase.setSeccion(clase.getSeccion().substring(0, 1));
        }
    }
}