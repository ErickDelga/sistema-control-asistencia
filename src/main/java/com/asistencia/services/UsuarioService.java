package com.asistencia.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.asistencia.model.Usuario;
import com.asistencia.repository.UsuarioRepository;

import java.util.List;
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario actualizar(Long id, Usuario usuario) {

        // 1️⃣ Buscar el usuario existente en la BD
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2️⃣ Actualizar campos (NO el ID)
        usuarioExistente.setUsername(usuario.getUsername());
        usuarioExistente.setRol(usuario.getRol());

        // 3️⃣ Actualizar password SOLO si viene en el request
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(
                    passwordEncoder.encode(usuario.getPassword())
            );
        }
        // 4️⃣ Guardar cambios
        return usuarioRepository.save(usuarioExistente);
    }
}
