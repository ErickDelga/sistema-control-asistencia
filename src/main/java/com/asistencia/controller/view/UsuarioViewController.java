package com.asistencia.controller.view;

import com.asistencia.model.Anio;
import com.asistencia.model.Rol;
import com.asistencia.model.TipoBachillerato;
import com.asistencia.model.Usuario;
import com.asistencia.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    private final UsuarioService usuarioService;

    public UsuarioViewController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("usuario", new Usuario());
        cargarCatalogos(model);
        model.addAttribute("modoEdicion", false);
        return "usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario, Model model) {
        try {
            normalizarCampos(usuario);
            usuarioService.guardar(usuario);
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("usuario", usuario);
            cargarCatalogos(model);
            model.addAttribute("modoEdicion", usuario.getId() != null);
            model.addAttribute("errorFormulario", ex.getMessage());
            return "usuarios/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            return "redirect:/usuarios";
        }

        usuario.setPassword("");

        model.addAttribute("usuario", usuario);
        cargarCatalogos(model);
        model.addAttribute("modoEdicion", true);

        return "usuarios/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("roles", Rol.values());
        model.addAttribute("anios", Anio.values());
        model.addAttribute("tiposBachillerato", TipoBachillerato.values());
    }

    private void normalizarCampos(Usuario usuario) {
        if (usuario.getUsername() != null) {
            usuario.setUsername(usuario.getUsername().trim().toLowerCase());
        }

        if (usuario.getSeccionAsignada() != null) {
            usuario.setSeccionAsignada(usuario.getSeccionAsignada().trim().toUpperCase());
        }

        if (usuario.getRol() != Rol.DOCENTE) {
            usuario.setAnioAsignado(null);
            usuario.setTipoBachilleratoAsignado(null);
            usuario.setSeccionAsignada(null);
        }
    }
}