package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Categoria;
import com.universidad.catalogo.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listarTodas());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("titulo", "Nueva Categoría");
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Categoria categoria,
                          BindingResult result, Model model) {
        String titulo = categoria.getId() == null ? "Nueva Categoría" : "Editar Categoría";
        if (result.hasErrors()) {
            model.addAttribute("titulo", titulo);
            return "categorias/formulario";
        }
        try {
            service.guardar(categoria);
        } catch (IllegalStateException e) {
            // Regla de negocio (nombre duplicado): se muestra junto al campo
            result.rejectValue("nombre", "nombre.duplicado", e.getMessage());
            model.addAttribute("titulo", titulo);
            return "categorias/formulario";
        }
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", service.buscarPorId(id));
        model.addAttribute("titulo", "Editar Categoría");
        return "categorias/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", service.buscarPorId(id));
        return "categorias/confirmar-eliminar";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.eliminar(id);
        } catch (IllegalStateException e) {
            // Borrado rechazado (p. ej. categoría con productos): se informa en la lista
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }
}