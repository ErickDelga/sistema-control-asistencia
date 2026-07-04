package com.asistencia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authProvider());

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/favicon.ico"
                ).permitAll()

                .requestMatchers(
                        "/",
                        "/home",
                        "/dashboard",
                        "/dashboard/**",
                        "/admin/dashboard",
                        "/rectoria/dashboard",
                        "/docente/dashboard",
                        "/secretaria/dashboard"
                ).hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")

                // USUARIOS
                .requestMatchers("/usuarios/**")
                .hasRole("ADMIN")

                // ESTUDIANTES
                .requestMatchers(HttpMethod.GET, "/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.POST, "/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.PUT, "/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.DELETE, "/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")

                // CLASES
                .requestMatchers(HttpMethod.GET, "/clases/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.POST, "/clases/guardar")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE")
                .requestMatchers(HttpMethod.POST, "/clases/actualizar")
                .hasAnyRole("ADMIN", "RECTORIA")
                .requestMatchers("/clases/editar/**", "/clases/eliminar/**")
                .hasAnyRole("ADMIN", "RECTORIA")

                // ASISTENCIAS
                .requestMatchers(HttpMethod.GET, "/asistencias/**", "/asistencia/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.POST, "/asistencias/**", "/asistencia/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE")

                // REPORTES
                .requestMatchers(HttpMethod.GET, "/reportes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")

                // API ESTUDIANTES
                .requestMatchers(HttpMethod.GET, "/api/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.POST, "/api/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.PUT, "/api/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.DELETE, "/api/estudiantes/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")

                // API CLASES
                .requestMatchers(HttpMethod.GET, "/api/clases/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.POST, "/api/clases/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE")

                // API ASISTENCIAS
                .requestMatchers(HttpMethod.GET, "/api/asistencias/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE", "SECRETARIA")
                .requestMatchers(HttpMethod.POST, "/api/asistencias/**")
                .hasAnyRole("ADMIN", "RECTORIA", "DOCENTE")

                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );

        return http.build();
    }
}