package com.asistencia.services;

import com.asistencia.model.Usuario;
import com.asistencia.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CREAR
    // =========================
    public Usuario guardar(Usuario usuario) {

        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("Username ya existe");
        }

        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        return usuarioRepository.save(usuario);
    }

    // =========================
    // LISTAR
    // =========================
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // =========================
    // BUSCAR
    // =========================
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // =========================
    // ELIMINAR
    // =========================
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // =========================
    // ACTUALIZAR
    // =========================
    public Usuario actualizar(Long id, Usuario usuario) {

        Usuario existente = buscarPorId(id);

        existente.setUsername(usuario.getUsername());
        existente.setRol(usuario.getRol());

        // SOLO si viene password nueva
        if (usuario.getPassword() != null &&
                !usuario.getPassword().isBlank()) {

            existente.setPassword(
                    passwordEncoder.encode(usuario.getPassword())
            );
        }

        return usuarioRepository.save(existente);
    }
}
