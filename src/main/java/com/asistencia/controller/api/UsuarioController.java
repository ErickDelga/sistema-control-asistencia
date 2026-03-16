package com.asistencia.controller.api;

import com.asistencia.model.Usuario;
import com.asistencia.services.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ===============================
    // LISTAR TODOS
    // ===============================
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.listar();
        return ResponseEntity.ok(usuarios);
    }

    // ===============================
    // BUSCAR POR ID
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {

        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);
    }

    // ===============================
    // CREAR USUARIO
    // ===============================
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {

        Usuario nuevoUsuario = usuarioService.guardar(usuario);

        return ResponseEntity.ok(nuevoUsuario);
    }

    // ===============================
    // ACTUALIZAR USUARIO
    // ===============================
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        usuario.setId(id);

        Usuario usuarioActualizado = usuarioService.guardar(usuario);

        if (usuarioActualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuarioActualizado);
    }

    // ===============================
    // ELIMINAR USUARIO
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        usuarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

}