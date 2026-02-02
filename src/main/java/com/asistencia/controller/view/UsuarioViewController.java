package com.asistencia.controller.view;

import com.asistencia.model.Usuario;
import com.asistencia.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    private final UsuarioService service;

    public UsuarioViewController(UsuarioService service) {
        this.service = service;
    }

//    @GetMapping
//    public String vistaUsuarios() {
//        return "usuarios";
//    }
//
//    @PostMapping("/guardar")
//    public String guardar(Usuario u) {
//        service.guardar(u);
//        return "redirect:/usuarios";
//    }
// ✅ LISTAR + FORM
@GetMapping
public String vistaUsuarios(Model model) {

    model.addAttribute("usuarios", service.listarTodos());

    return "usuarios";
}

    // ✅ GUARDAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario u,
                          RedirectAttributes ra) {

        try {
            service.guardar(u);
            ra.addFlashAttribute("ok", "Usuario creado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al crear usuario");
        }

        return "redirect:/usuarios";
    }

}
