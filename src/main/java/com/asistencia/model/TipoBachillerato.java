package com.asistencia.model;

public enum TipoBachillerato {
    BACHILLERATO_GENERAL("Bachillerato General"),
    TECNICO_PRODUCTIVO_EN_SALUD_Y_BIENESTAR_SOCIAL("Técnico Productivo en Salud y Bienestar Social"),
    ADMINISTRATIVO_CONTABLE("Administrativo Contable"),
    INFRAESTRUCTURA_TECNOLOGICA_Y_SERVICIOS_INFORMATICOS("Infraestructura Tecnológica y Servicios Informáticos");

    private final String nombreVisible;

    TipoBachillerato(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }
}