package com.asistencia.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        var roles = authentication.getAuthorities();

        if (roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            response.sendRedirect("/admin/dashboard");
        }
        else if (roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"))) {
            response.sendRedirect("/docente/dashboard");
        }
        else if (roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SECRETARIA"))) {
            response.sendRedirect("/secretaria/dashboard");
        }
        else {
            response.sendRedirect("/");
        }
    }
}
