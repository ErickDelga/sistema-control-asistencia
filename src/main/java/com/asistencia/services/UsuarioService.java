package com.asistencia.services;

import com.asistencia.model.Anio;
import com.asistencia.model.Rol;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.model.Usuario;
import com.asistencia.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarDocentes() {
        return usuarioRepository.findByRol(Rol.DOCENTE);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null) {
            return crear(usuario);
        }
        return actualizarDesdeFormulario(usuario);
    }

    private Usuario crear(Usuario usuario) {
        validarDatosBasicos(usuario, true);

        String usernameFinal = generarUsername(usuario.getNombreCompleto(), null);

        usuario.setNombreCompleto(limpiarTexto(usuario.getNombreCompleto()));
        usuario.setUsername(usernameFinal);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        aplicarAsignacionDocente(usuario, null);

        return usuarioRepository.save(usuario);
    }

    private Usuario actualizarDesdeFormulario(Usuario datos) {
        Usuario usuario = usuarioRepository.findById(datos.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        validarDatosBasicos(datos, false);

        usuario.setNombreCompleto(limpiarTexto(datos.getNombreCompleto()));
        usuario.setRol(datos.getRol());

        String usernameFinal;
        if (datos.getUsername() == null || datos.getUsername().isBlank()) {
            usernameFinal = generarUsername(datos.getNombreCompleto(), usuario.getId());
        } else {
            usernameFinal = normalizarUsername(datos.getUsername());
            if (usernameFinal.isBlank()) {
                usernameFinal = generarUsername(datos.getNombreCompleto(), usuario.getId());
            } else {
                usernameFinal = asegurarUsernameUnico(usernameFinal, usuario.getId());
            }
        }

        usuario.setUsername(usernameFinal);

        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        usuario.setAnioAsignado(datos.getAnioAsignado());
        usuario.setTipoBachilleratoAsignado(datos.getTipoBachilleratoAsignado());
        usuario.setSeccionAsignada(datos.getSeccionAsignada());

        aplicarAsignacionDocente(usuario, usuario.getId());

        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public String generarUsername(String nombreCompleto) {
        return generarUsername(nombreCompleto, null);
    }

    public String generarUsername(String nombreCompleto, Long usuarioIdActual) {
        String nombreLimpio = limpiarTexto(nombreCompleto);

        if (nombreLimpio.isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }

        String[] partes = nombreLimpio.split("\\s+");

        String primerNombre = obtenerPrimerNombre(partes);
        String primerApellido = obtenerPrimerApellido(partes);

        String parteNombre = tomar(primerNombre, 2);
        String parteApellido = tomar(primerApellido, 6);

        String base = normalizarUsername(parteNombre + parteApellido);

        if (base.isBlank()) {
            base = "usuario";
        }

        return asegurarUsernameUnico(base, usuarioIdActual);
    }

    private String asegurarUsernameUnico(String base, Long usuarioIdActual) {
        String username = base;
        int contador = 1;

        while (true) {
            Optional<Usuario> existente = usuarioRepository.findByUsername(username);

            if (existente.isEmpty()) {
                return username;
            }

            if (usuarioIdActual != null && existente.get().getId().equals(usuarioIdActual)) {
                return username;
            }

            username = base + contador;
            contador++;
        }
    }

    private void aplicarAsignacionDocente(Usuario usuario, Long usuarioIdActual) {
        if (usuario.getRol() == Rol.DOCENTE) {
            validarAsignacionDocenteCompleta(usuario);

            usuario.setSeccionAsignada(usuario.getSeccionAsignada().trim().toUpperCase());
            if (usuario.getSeccionAsignada().length() > 1) {
                usuario.setSeccionAsignada(usuario.getSeccionAsignada().substring(0, 1));
            }

            validarAsignacionDocenteUnica(
                    usuario.getAnioAsignado(),
                    usuario.getTipoBachilleratoAsignado(),
                    usuario.getSeccionAsignada(),
                    usuarioIdActual
            );
        } else {
            usuario.setAnioAsignado(null);
            usuario.setTipoBachilleratoAsignado(null);
            usuario.setSeccionAsignada(null);
        }
    }

    private void validarAsignacionDocenteCompleta(Usuario usuario) {
        if (usuario.getAnioAsignado() == null
                || usuario.getTipoBachilleratoAsignado() == null
                || usuario.getSeccionAsignada() == null
                || usuario.getSeccionAsignada().isBlank()) {
            throw new IllegalArgumentException("Debes asignar año, tipo de bachillerato y sección al docente");
        }
    }

    private void validarAsignacionDocenteUnica(Anio anio,
                                               TipoBachillerato tipo,
                                               String seccion,
                                               Long usuarioIdActual) {

        Optional<Usuario> existente = usuarioRepository
                .findByRolAndAnioAsignadoAndTipoBachilleratoAsignadoAndSeccionAsignada(
                        Rol.DOCENTE, anio, tipo, seccion
                );

        if (existente.isPresent()) {
            if (usuarioIdActual == null || !existente.get().getId().equals(usuarioIdActual)) {
                throw new IllegalArgumentException(
                        "Esa asignación ya está a cargo de otro docente."
                );
            }
        }
    }

    private void validarDatosBasicos(Usuario usuario, boolean validarPassword) {
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }

        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        if (validarPassword && (usuario.getPassword() == null || usuario.getPassword().isBlank())) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
    }

    private String obtenerPrimerNombre(String[] partes) {
        if (partes.length >= 1) {
            return partes[0];
        }
        return "us";
    }

    private String obtenerPrimerApellido(String[] partes) {
        /*
         * Reglas:
         * 2 palabras: nombre + apellido -> apellido = partes[1]
         * 3 palabras: nombre + nombre + apellido -> apellido = partes[2]
         * 4 palabras: nombre + nombre + apellido + apellido -> apellido = partes[2]
         */
        if (partes.length >= 3) {
            return partes[2];
        }

        if (partes.length >= 2) {
            return partes[1];
        }

        return "user";
    }

    private String tomar(String texto, int cantidad) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        return texto.substring(0, Math.min(cantidad, texto.length()));
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim().replaceAll("\\s+", " ");
    }

    private String normalizarUsername(String texto) {
        if (texto == null) {
            return "";
        }

        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return sinTildes
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }
}