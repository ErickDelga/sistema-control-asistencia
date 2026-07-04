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

        if (esAdminORectoria(usuarioActual)) {
            if (clase.getDocente() == null || clase.getDocente().getId() == null) {
                throw new IllegalArgumentException("Debes seleccionar un docente");
            }

            Usuario docente = usuarioService.buscarPorId(clase.getDocente().getId());

            if (docente == null || docente.getRol() != Rol.DOCENTE) {
                throw new IllegalArgumentException("El usuario seleccionado no es un docente válido");
            }

            clase.setDocente(docente);

        } else if (esDocente(usuarioActual)) {
            clase.setDocente(usuarioActual);

        } else {
            throw new IllegalArgumentException("No tienes permisos para crear clases");
        }

        normalizarClase(clase);
        clase.setFechaCreacion(LocalDateTime.now());

        return repo.save(clase);
    }

    public Clase actualizarClase(Clase datos) {
        Authentication auth = obtenerAutenticacionValida();
        Usuario usuarioActual = usuarioService.buscarPorUsername(auth.getName());

        if (!esAdminORectoria(usuarioActual)) {
            throw new IllegalArgumentException("No tienes permisos para editar clases");
        }

        Clase clase = repo.findById(datos.getId())
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        clase.setAsignatura(datos.getAsignatura());
        clase.setAnio(datos.getAnio());
        clase.setTipoBachillerato(datos.getTipoBachillerato());
        clase.setSeccion(datos.getSeccion());

        if (datos.getDocente() == null || datos.getDocente().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un docente");
        }

        Usuario docente = usuarioService.buscarPorId(datos.getDocente().getId());

        if (docente == null || docente.getRol() != Rol.DOCENTE) {
            throw new IllegalArgumentException("El usuario seleccionado no es un docente válido");
        }

        clase.setDocente(docente);

        normalizarClase(clase);

        return repo.save(clase);
    }

    public void eliminarClase(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("La clase no existe");
        }
        repo.deleteById(id);
    }

    public List<Clase> listar() {
        return repo.findAll();
    }

    public List<Clase> listarParaUsuarioActual() {
        Usuario usuarioActual = obtenerUsuarioActual();

        if (usuarioActual == null) {
            return Collections.emptyList();
        }

        if (esAdminORectoria(usuarioActual) || esSecretaria(usuarioActual)) {
            return repo.findAll();
        }

        if (esDocente(usuarioActual)) {
            return repo.findByDocenteId(usuarioActual.getId());
        }

        return Collections.emptyList();
    }

    public Clase buscarPorId(Long id) {
        Usuario usuarioActual = obtenerUsuarioActual();

        Clase clase = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        if (usuarioActual == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

        if (esAdminORectoria(usuarioActual) || esSecretaria(usuarioActual)) {
            return clase;
        }

        if (esDocente(usuarioActual)) {
            if (clase.getDocente() == null
                    || clase.getDocente().getId() == null
                    || !clase.getDocente().getId().equals(usuarioActual.getId())) {
                throw new IllegalArgumentException("No tienes permisos para acceder a esta clase");
            }
            return clase;
        }

        throw new IllegalArgumentException("No tienes permisos para acceder a esta clase");
    }

    private Authentication obtenerAutenticacionValida() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

        return auth;
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return usuarioService.buscarPorUsername(auth.getName());
    }

    private boolean esAdminORectoria(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && (usuario.getRol() == Rol.ADMIN || usuario.getRol() == Rol.RECTORIA);
    }

    private boolean esDocente(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol() == Rol.DOCENTE;
    }

    private boolean esSecretaria(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol() == Rol.SECRETARIA;
    }

    private void normalizarClase(Clase clase) {
        if (clase.getAsignatura() == null || clase.getAsignatura().isBlank()) {
            throw new IllegalArgumentException("La asignatura es obligatoria");
        }

        if (clase.getAnio() == null) {
            throw new IllegalArgumentException("El año es obligatorio");
        }

        if (clase.getTipoBachillerato() == null) {
            throw new IllegalArgumentException("El tipo de bachillerato es obligatorio");
        }

        if (clase.getSeccion() == null || clase.getSeccion().isBlank()) {
            throw new IllegalArgumentException("La sección es obligatoria");
        }

        clase.setAsignatura(clase.getAsignatura().trim().replaceAll("\\s+", " "));
        clase.setSeccion(clase.getSeccion().trim().toUpperCase());

        if (clase.getSeccion().length() > 1) {
            clase.setSeccion(clase.getSeccion().substring(0, 1));
        }
    }
}