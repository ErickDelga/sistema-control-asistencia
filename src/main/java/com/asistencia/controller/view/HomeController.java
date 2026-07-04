package com.asistencia.controller.view;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Authentication auth) {

        if (auth == null) {
            return "redirect:/login";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/dashboard";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECTORIA"))) {
            return "redirect:/dashboard";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"))) {
            return "redirect:/dashboard";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SECRETARIA"))) {
            return "redirect:/dashboard";
        }

        return "redirect:/dashboard";
    }
}