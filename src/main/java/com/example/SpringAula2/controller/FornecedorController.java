package com.example.SpringAula2.controller;

import com.example.SpringAula2.dto.FornecedorProdutoCountDTO;
import com.example.SpringAula2.model.Fornecedor;
import com.example.SpringAula2.service.FornecedorService;
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
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fornecedores", fornecedorService.listarTodos());
        return "fornecedores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("fornecedor", new Fornecedor());
        return "fornecedores/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Fornecedor fornecedor) {
        if (fornecedor.getId() != null) {
            fornecedorService.atualizar(fornecedor.getId(), fornecedor);
        } else {
            fornecedorService.salvar(fornecedor);
        }
        return "redirect:/fornecedores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Fornecedor fornecedor = fornecedorService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor inválido: " + id));
        model.addAttribute("fornecedor", fornecedor);
        return "fornecedores/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        fornecedorService.deletar(id);
        return "redirect:/fornecedores";
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=fornecedores.pdf");

        List<Fornecedor> fornecedores = fornecedorService.listarTodos();

        com.lowagie.text.Document document = new com.lowagie.text.Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph("Lista de Fornecedores"));
        document.add(new Paragraph(" ")); // Espaço em branco

        PdfPTable tabela = new PdfPTable(2); // 2 colunas: Nome, CNPJ
        tabela.setWidthPercentage(100);

        PdfPCell cabecalho1 = new PdfPCell(new Phrase("Nome"));
        tabela.addCell(cabecalho1);

        PdfPCell cabecalho2 = new PdfPCell(new Phrase("CNPJ"));
        tabela.addCell(cabecalho2);

        for (Fornecedor fornecedor : fornecedores) {
            tabela.addCell(fornecedor.getNome());
            tabela.addCell(fornecedor.getCnpj());
        }

        document.add(tabela);
        document.close();
    }

    @GetMapping("/contar-produtos")
    public String contarProdutosPorFornecedor(Model model) {
        List<FornecedorProdutoCountDTO> contagemProdutos = fornecedorService.contarProdutosPorFornecedor();
        model.addAttribute("contagemProdutos", contagemProdutos);
        return "fornecedores/contar-produtos";
    }

}

