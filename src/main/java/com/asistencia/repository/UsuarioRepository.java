package com.asistencia.repository;

import com.asistencia.model.Anio;
import com.asistencia.model.Rol;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findByRol(Rol rol);

    Optional<Usuario> findByRolAndAnioAsignadoAndTipoBachilleratoAsignadoAndSeccionAsignada(
            Rol rol,
            Anio anioAsignado,
            TipoBachillerato tipoBachilleratoAsignado,
            String seccionAsignada
    );
}