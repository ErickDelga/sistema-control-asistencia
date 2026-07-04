package com.asistencia.controller.view;

import com.asistencia.model.Estudiante;
import com.asistencia.services.EstudianteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteViewController {

    private static final String CARPETA_UPLOADS = "uploads";
    private static final String CARPETA_FOTOS = "uploads/fotos";

    private final EstudianteService estudianteService;

    public EstudianteViewController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        return "estudiantes/lista";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("modoEdicion", false);
        return "estudiantes/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Estudiante estudiante = estudianteService.buscarPorId(id);

        if (estudiante == null) {
            return "redirect:/estudiantes";
        }

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("modoEdicion", true);
        return "estudiantes/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Estudiante estudiante,
                          @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
                          RedirectAttributes redirectAttributes) {

        try {
            normalizarCampos(estudiante);

            Estudiante estudianteExistente = null;
            if (estudiante.getId() != null) {
                estudianteExistente = estudianteService.buscarPorId(estudiante.getId());
            }

            if (estudianteExistente != null && (fotoFile == null || fotoFile.isEmpty())) {
                estudiante.setFoto(estudianteExistente.getFoto());
            }

            if (fotoFile != null && !fotoFile.isEmpty()) {
                validarFoto(fotoFile);
                String nombreArchivo = guardarFoto(fotoFile);
                estudiante.setFoto(nombreArchivo);
            }

            estudianteService.guardar(estudiante);

            redirectAttributes.addFlashAttribute("success", "Estudiante guardado correctamente.");
            return "redirect:/estudiantes";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estudiantes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al guardar el estudiante.");
            return "redirect:/estudiantes";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Estudiante eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/estudiantes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id,
                          @RequestParam(required = false) String motivo,
                          RedirectAttributes redirectAttributes) {
        try {
            estudianteService.activarManual(id, motivo);
            redirectAttributes.addFlashAttribute("success", "Estudiante activado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/estudiantes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id,
                             @RequestParam(required = false) String motivo,
                             RedirectAttributes redirectAttributes) {
        try {
            estudianteService.desactivarManual(id, motivo);
            redirectAttributes.addFlashAttribute("success", "Estudiante desactivado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/estudiantes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @GetMapping("/subir-csv")
    public String mostrarFormularioCsv() {
        return "estudiantes/subir-csv";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE','SECRETARIA')")
    @PostMapping("/subir-csv")
    public String subirCsv(@RequestParam("archivo") MultipartFile archivo,
                           RedirectAttributes redirectAttributes) {
        try {
            int cantidad = estudianteService.importarDesdeCsv(archivo);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "CSV procesado correctamente. Estudiantes importados: " + cantidad
            );
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estudiantes/subir-csv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al procesar el archivo CSV.");
            return "redirect:/estudiantes/subir-csv";
        }

        return "redirect:/estudiantes";
    }

    private void normalizarCampos(Estudiante estudiante) {
        if (estudiante.getNombres() != null) {
            estudiante.setNombres(estudiante.getNombres().trim().replaceAll("\\s+", " "));
        }

        if (estudiante.getApellidos() != null) {
            estudiante.setApellidos(estudiante.getApellidos().trim().replaceAll("\\s+", " "));
        }

        if (estudiante.getSeccion() != null) {
            estudiante.setSeccion(estudiante.getSeccion().trim().toUpperCase());
            if (estudiante.getSeccion().length() > 1) {
                estudiante.setSeccion(estudiante.getSeccion().substring(0, 1));
            }
        }
    }

    private void validarFoto(MultipartFile fotoFile) {
        String contentType = fotoFile.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen válida.");
        }

        long maxBytes = 5 * 1024 * 1024; // 5 MB
        if (fotoFile.getSize() > maxBytes) {
            throw new IllegalArgumentException("La foto no puede superar los 5 MB.");
        }
    }

    private String guardarFoto(MultipartFile fotoFile) throws IOException {
        Path carpetaUploads = Paths.get(CARPETA_UPLOADS);
        Path carpetaFotos = Paths.get(CARPETA_FOTOS);

        if (!Files.exists(carpetaUploads)) {
            Files.createDirectories(carpetaUploads);
        }

        if (!Files.exists(carpetaFotos)) {
            Files.createDirectories(carpetaFotos);
        }

        String nombreOriginal = fotoFile.getOriginalFilename();
        String extension = obtenerExtension(nombreOriginal);
        String nombreArchivo = UUID.randomUUID() + extension;

        Path rutaCompleta = carpetaFotos.resolve(nombreArchivo);
        Files.copy(fotoFile.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

        return "fotos/" + nombreArchivo;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }
}