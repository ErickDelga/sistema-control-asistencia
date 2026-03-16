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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteViewController {

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

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        return "estudiantes/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("estudiante", estudianteService.buscarPorId(id));
        return "estudiantes/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Estudiante estudiante,
                          @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
                          RedirectAttributes redirectAttributes) {

        try {
            Estudiante estudianteExistente = null;

            if (estudiante.getId() != null) {
                estudianteExistente = estudianteService.buscarPorId(estudiante.getId());
            }

            if (estudianteExistente != null && (fotoFile == null || fotoFile.isEmpty())) {
                estudiante.setFoto(estudianteExistente.getFoto());
            }

            if (fotoFile != null && !fotoFile.isEmpty()) {
                String nombreArchivo = guardarFoto(fotoFile);
                estudiante.setFoto(nombreArchivo);
            }

            estudianteService.guardar(estudiante);

            redirectAttributes.addFlashAttribute("success", "Estudiante guardado correctamente.");
            return "redirect:/estudiantes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar el estudiante.");
            return "redirect:/estudiantes";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Estudiante eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el estudiante.");
        }
        return "redirect:/estudiantes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
    @GetMapping("/subir-csv")
    public String mostrarFormularioCsv() {
        return "estudiantes/subir-csv";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECTORIA','DOCENTE')")
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
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Ocurrió un error al procesar el archivo CSV."
            );
            return "redirect:/estudiantes/subir-csv";
        }

        return "redirect:/estudiantes";
    }

    private String guardarFoto(MultipartFile fotoFile) throws IOException {
        Path carpeta = Paths.get(CARPETA_FOTOS);

        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        String nombreOriginal = fotoFile.getOriginalFilename();
        String extension = obtenerExtension(nombreOriginal);
        String nombreArchivo = System.currentTimeMillis() + "_" + System.nanoTime() + extension;

        Path rutaCompleta = carpeta.resolve(nombreArchivo);
        Files.copy(fotoFile.getInputStream(), rutaCompleta);

        return "fotos/" + nombreArchivo;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }
}