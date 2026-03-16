package com.asistencia.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarPassword {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "12345";

        String passwordEncriptada = encoder.encode(password);

        System.out.println("Password original: " + password);
        System.out.println("Password encriptada: " + passwordEncriptada);
    }

}
