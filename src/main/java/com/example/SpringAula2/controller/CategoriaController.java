package com.example.SpringAula2.controller;

import com.example.SpringAula2.dto.CategoriaProdutoCountDTO;
import com.example.SpringAula2.model.Categoria;
import com.example.SpringAula2.service.CategoriaService;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "categorias/lista";
    }

    @GetMapping("/nova")
    public String novaCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/form";
    }

    @PostMapping("/salvar")
    public String salvarCategoria(@ModelAttribute Categoria categoria) {
        if (categoria.getId() != null) {
            categoriaService.atualizar(categoria.getId(), categoria);
        } else {
            categoriaService.salvar(categoria);
        }
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida: " + id));
        model.addAttribute("categoria", categoria);
        return "categorias/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluirCategoria(@PathVariable Long id) {
        categoriaService.deletar(id);
        return "redirect:/categorias";
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=categorias.pdf");

        List<Categoria> categorias = categoriaService.listarTodas();

        com.lowagie.text.Document document = new com.lowagie.text.Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph("Lista de Categorias"));
        document.add(new Paragraph(" ")); // Espaço em branco

        PdfPTable tabela = new PdfPTable(1); // 1 coluna: Nome
        tabela.setWidthPercentage(100);

        PdfPCell cabecalho1 = new PdfPCell(new Phrase("Nome"));
        tabela.addCell(cabecalho1);

        for (Categoria categoria : categorias) {
            tabela.addCell(categoria.getNome());
        }

        document.add(tabela);
        document.close();
    }

    @GetMapping("/contar-produtos")
    public String contarProdutosPorCategoria(Model model) {
        List<CategoriaProdutoCountDTO> categorias = categoriaService.contarProdutosPorCategoria();
        model.addAttribute("categorias", categorias);
        return "categorias/contar-produtos";
    }

}
