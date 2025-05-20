package com.example.SpringAula2.controller;

import com.example.SpringAula2.model.ItemCardapio;
import com.example.SpringAula2.service.ItemCardapioService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cardapio")
public class ItemCardapioController {

    @Autowired
    private ItemCardapioService service;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending());
        Page<ItemCardapio> pagina = service.listarPaginado(pageable);
        model.addAttribute("itens", pagina);
        return "cardapio/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("item", new ItemCardapio());
        model.addAttribute("categorias", ItemCardapio.Categoria.values());
        return "cardapio/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ItemCardapio item) {
        service.salvar(item);
        return "redirect:/cardapio";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        ItemCardapio item = service.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        model.addAttribute("item", item);
        model.addAttribute("categorias", ItemCardapio.Categoria.values());
        return "cardapio/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/cardapio";
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=cardapio.pdf");

        List<ItemCardapio> itens = service.listarTodos();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Lista de Itens do Cardápio"));
        document.add(new Paragraph(" "));

        PdfPTable tabela = new PdfPTable(3);
        tabela.setWidthPercentage(100);

        tabela.addCell(new PdfPCell(new Phrase("Nome")));
        tabela.addCell(new PdfPCell(new Phrase("Preço")));
        tabela.addCell(new PdfPCell(new Phrase("Categoria")));

        for (ItemCardapio item : itens) {
            tabela.addCell(item.getNome());
            tabela.addCell(item.getPreco() != null ? String.format("%.2f", item.getPreco()) : "");
            tabela.addCell(item.getCategoria() != null ? item.getCategoria().name() : "");
        }

        document.add(tabela);
        document.close();
    }

    @GetMapping("/buscar-por-preco")
    public String buscarPorPrecoForm() {
        return "cardapio/busca-preco";
    }

    @GetMapping("/buscar-por-preco/resultados")
    public String buscarPorPrecoResultados(@RequestParam Double min, @RequestParam Double max, Model model) {
        model.addAttribute("itens", service.buscarPorPrecoEntre(min, max));
        return "cardapio/busca-preco";
    }
}

