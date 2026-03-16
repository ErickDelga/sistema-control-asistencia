package com.asistencia.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public String manejarErrorNegocio(IllegalArgumentException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String manejarErrorGeneral(Exception ex, Model model) {
        model.addAttribute("mensaje", "Ha ocurrido un error inesperado.");
        return "error";
    }
}
