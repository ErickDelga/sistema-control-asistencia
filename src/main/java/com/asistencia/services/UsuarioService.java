package com.asistencia.services;

import com.asistencia.model.Rol;
import com.asistencia.model.Usuario;
import com.asistencia.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

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
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().isBlank()) {
            throw new RuntimeException("El nombre completo es obligatorio");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }

        String usernameFinal;

        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            usernameFinal = generarUsername(usuario.getNombreCompleto());
        } else {
            usernameFinal = normalizarUsername(usuario.getUsername());
            usernameFinal = asegurarUsernameUnico(usernameFinal, null, usuario.getNombreCompleto());
        }

        usuario.setUsername(usernameFinal);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    private Usuario actualizarDesdeFormulario(Usuario datos) {
        Usuario usuario = usuarioRepository.findById(datos.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (datos.getNombreCompleto() == null || datos.getNombreCompleto().isBlank()) {
            throw new RuntimeException("El nombre completo es obligatorio");
        }

        usuario.setNombreCompleto(datos.getNombreCompleto());
        usuario.setRol(datos.getRol());

        String usernameFinal;

        if (datos.getUsername() == null || datos.getUsername().isBlank()) {
            usernameFinal = generarUsername(datos.getNombreCompleto(), usuario.getId());
        } else {
            usernameFinal = normalizarUsername(datos.getUsername());
            usernameFinal = asegurarUsernameUnico(usernameFinal, usuario.getId(), datos.getNombreCompleto());
        }

        usuario.setUsername(usernameFinal);

        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public String generarUsername(String nombreCompleto) {
        return generarUsername(nombreCompleto, null);
    }

    public String generarUsername(String nombreCompleto, Long usuarioIdActual) {
        String nombreLimpio = limpiarTexto(nombreCompleto);

        String[] partes = nombreLimpio.split("\\s+");

        String primerNombre = partes.length > 0 ? partes[0] : "us";
        String apellidoBase = obtenerApellidoParaUsername(partes);

        String base = tomar(primerNombre, 2) + tomar(apellidoBase, 6);
        base = normalizarUsername(base);

        if (base.isBlank()) {
            base = "usuario";
        }

        return asegurarUsernameUnico(base, usuarioIdActual, nombreCompleto);
    }

    private String asegurarUsernameUnico(String base, Long usuarioIdActual, String nombreCompleto) {
        String username = base;
        int contador = 1;

        while (true) {
            var existente = usuarioRepository.findByUsername(username);

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

    private String obtenerApellidoParaUsername(String[] partes) {
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