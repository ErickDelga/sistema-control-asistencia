package com.asistencia.controller.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public String test() {
        return "Sistema de Asistencia activo";
    }
}
