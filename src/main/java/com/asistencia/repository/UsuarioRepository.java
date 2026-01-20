package com.asistencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asistencia.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
