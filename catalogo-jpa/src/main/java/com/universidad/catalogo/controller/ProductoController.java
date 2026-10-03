package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.CategoriaService;
import com.universidad.catalogo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService p, CategoriaService c) {
        this.productoService = p;
        this.categoriaService = c;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("titulo", "Nuevo Producto");
        return "productos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categoriaId", producto.getCategoria().getId());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("titulo", "Editar Producto");
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Producto producto,
                          BindingResult result,
                          @RequestParam Long categoriaId,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("categoriaId", categoriaId);
            model.addAttribute("titulo",
                    producto.getId() == null ? "Nuevo Producto" : "Editar Producto");
            return "productos/formulario";
        }
        productoService.guardar(producto, categoriaId);
        return "redirect:/productos";
    }

    // POST: eliminar modifica estado, no debe hacerse con GET
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }

    // Recibe el formulario de la lista y redirige a la URL con la categoría en la ruta
    @GetMapping("/filtrar")
    public String filtrar(@RequestParam Long categoriaId, @RequestParam BigDecimal minimo) {
        return "redirect:/productos/categoria/" + categoriaId
                + "/precio-mayor?minimo=" + minimo.toPlainString();
    }

    // Endpoint que usa la consulta JPQL personalizada del repositorio
    @GetMapping("/categoria/{categoriaId}/precio-mayor")
    public String porCategoriaConPrecioMayor(@PathVariable Long categoriaId,
                                             @RequestParam BigDecimal minimo,
                                             Model model) {
        model.addAttribute("productos",
                productoService.listarPorCategoriaConPrecioMayorA(categoriaId, minimo));
        model.addAttribute("categoria", categoriaService.buscarPorId(categoriaId));
        model.addAttribute("minimo", minimo);
        return "productos/filtrados";
    }
}