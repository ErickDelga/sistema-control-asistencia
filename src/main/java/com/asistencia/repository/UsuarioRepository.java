package com.asistencia.repository;

import com.asistencia.model.Usuario;
import com.asistencia.model.Rol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por username (para login)
    Optional<Usuario> findByUsername(String username);

    // Verificar si un username ya existe
    boolean existsByUsername(String username);

    // Listar usuarios por rol
    List<Usuario> findByRol(Rol rol);

}