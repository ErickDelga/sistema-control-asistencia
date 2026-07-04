package com.asistencia.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String manejarErrorNegocio(IllegalArgumentException ex,
                                      HttpServletRequest request,
                                      Model model) {
        logger.warn("Error de negocio en {}: {}", request.getRequestURI(), ex.getMessage());

        model.addAttribute("mensaje", ex.getMessage());
        model.addAttribute("detalle", "Error de validación o lógica de negocio");
        model.addAttribute("ruta", request.getRequestURI());

        return "error";
    }

    @ExceptionHandler(MultipartException.class)
    public String manejarErrorMultipart(MultipartException ex,
                                        HttpServletRequest request,
                                        Model model) {
        logger.error("Error multipart en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        Throwable causa = ex.getMostSpecificCause();

        model.addAttribute("mensaje", "Error al procesar el archivo enviado.");
        model.addAttribute("detalle", causa != null ? causa.getMessage() : ex.getMessage());
        model.addAttribute("ruta", request.getRequestURI());

        return "error";
    }

    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public String manejarRecursoNoEncontrado(Exception ex,
                                             HttpServletRequest request,
                                             Model model) {

        String uri = request.getRequestURI();

        if ("/favicon.ico".equals(uri) || uri.contains("preview-service-worker.js")) {
            logger.warn("Recurso estático no encontrado: {}", uri);
        } else {
            logger.warn("Ruta o recurso no encontrado: {}", uri);
        }

        model.addAttribute("mensaje", "El recurso solicitado no existe.");
        model.addAttribute("detalle", uri);
        model.addAttribute("ruta", uri);

        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String manejarErrorGeneral(Exception ex,
                                      HttpServletRequest request,
                                      Model model) {
        logger.error("Error general no controlado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        model.addAttribute("mensaje", "Ha ocurrido un error inesperado.");
        model.addAttribute("detalle", ex.getClass().getName());
        model.addAttribute("ruta", request.getRequestURI());

        return "error";
    }
}