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

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    public Usuario guardar(Usuario usuario) {
//        return usuarioRepository.save(usuario);
//    }

// =========================
// ✅ CREAR USUARIO
// =========================
public Usuario guardar(Usuario usuario) {

    // validar username duplicado
    if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
        throw new RuntimeException("El usuario ya existe");
    }

    // encriptar password
    usuario.setPassword(
            passwordEncoder.encode(usuario.getPassword())
    );

    return usuarioRepository.save(usuario);
}
//
//    public List<Usuario> listar() {
//        return usuarioRepository.findAll();
//    }
//
//    public void eliminar(Long id) {
//        usuarioRepository.deleteById(id);
//    }
//
//    public Usuario actualizar(Long id, Usuario usuario) {
//
//        // 1️⃣ Buscar el usuario existente en la BD
//        Usuario usuarioExistente = usuarioRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//
//        // 2️⃣ Actualizar campos (NO el ID)
//        usuarioExistente.setUsername(usuario.getUsername());
//        usuarioExistente.setRol(usuario.getRol());
//
//        // 3️⃣ Actualizar password SOLO si viene en el request
//        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
//            usuarioExistente.setPassword(
//                    passwordEncoder.encode(usuario.getPassword())
//            );
//        }
//        // 4️⃣ Guardar cambios
//        return usuarioRepository.save(usuarioExistente);
//    }
// =========================
// ✅ LISTAR
// =========================
public List<Usuario> listarTodos() {
    return usuarioRepository.findAll();
}

    // (mantengo este por compatibilidad si ya lo usas)
    public List<Usuario> listar() {
        return listarTodos();
    }

    // =========================
    // ✅ BUSCAR
    // =========================
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // =========================
    // ✅ ELIMINAR
    // =========================
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // =========================
    // ✅ ACTUALIZAR
    // =========================
    public Usuario actualizar(Long id, Usuario usuario) {

        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existente.setUsername(usuario.getUsername());
        existente.setRol(usuario.getRol());

        // actualizar password solo si viene nueva
        if (usuario.getPassword() != null &&
                !usuario.getPassword().isBlank()) {

            existente.setPassword(
                    passwordEncoder.encode(usuario.getPassword())
            );
        }

        return usuarioRepository.save(existente);
    }
}
