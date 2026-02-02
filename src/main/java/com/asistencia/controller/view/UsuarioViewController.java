package com.asistencia.controller.view;

import com.asistencia.model.Usuario;
import com.asistencia.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    private final UsuarioService service;

    public UsuarioViewController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String vistaUsuarios() {
        return "usuarios";
    }

    @PostMapping("/guardar")
    public String guardar(Usuario u) {
        service.guardar(u);
        return "redirect:/usuarios";
    }

}
