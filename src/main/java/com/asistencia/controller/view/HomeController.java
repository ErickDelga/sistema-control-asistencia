package com.asistencia.controller.view;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }
}