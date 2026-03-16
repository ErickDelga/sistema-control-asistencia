package com.asistencia.controller.view;

import com.asistencia.model.Rol;
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

    // ===============================
    // LISTAR USUARIOS
    // ===============================
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    // ===============================
    // FORM CREAR USUARIO
    // ===============================
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("modoEdicion", false);
        return "usuarios/form";
    }

    // ===============================
    // GUARDAR USUARIO
    // ===============================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    // ===============================
    // EDITAR USUARIO
    // ===============================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            return "redirect:/usuarios";
        }

        // no mostrar password real en el formulario
        usuario.setPassword("");

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("modoEdicion", true);

        return "usuarios/form";
    }

    // ===============================
    // ELIMINAR USUARIO
    // ===============================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }
}