package com.example.SpringAula2.controller;

import com.example.SpringAula2.model.Produto;
import com.example.SpringAula2.service.CategoriaService;
import com.example.SpringAula2.service.FornecedorService;
import com.example.SpringAula2.service.ProdutoService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private FornecedorService fornecedorService;
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarProdutos(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").ascending());
        Page<Produto> pagina = produtoService.listarPaginado(pageable);
        model.addAttribute("produtos", pagina);
        return "produtos/lista";
    }



    @GetMapping("/novo")
    public String novoProduto(Model model) {
        Produto produto = new Produto();
        produto.setFornecedores(new ArrayList<>());
        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("fornecedores", fornecedorService.listarTodos());
        return "produtos/form";
    }


    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("fornecedores", fornecedorService.listarTodos());
        return "produtos/form";
    }


    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        if (produto.getId() != null) {
            produtoService.atualizar(produto.getId(), produto);
        } else {
            produtoService.salvar(produto);
        }
        return "redirect:/produtos";
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return "redirect:/produtos";
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=produtos.pdf");

        List<Produto> produtos = produtoService.listarTodos();

        com.lowagie.text.Document document = new com.lowagie.text.Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph("Lista de Produtos"));
        document.add(new Paragraph(" ")); // Espaço em branco

        PdfPTable tabela = new PdfPTable(3); // 3 colunas: Nome, Preço e Categoria
        tabela.setWidthPercentage(100);

        PdfPCell cabecalho1 = new PdfPCell(new Phrase("Nome"));
        tabela.addCell(cabecalho1);

        PdfPCell cabecalho2 = new PdfPCell(new Phrase("Preço"));
        tabela.addCell(cabecalho2);

        PdfPCell cabecalho3 = new PdfPCell(new Phrase("Categoria"));
        tabela.addCell(cabecalho3);

        for (Produto produto : produtos) {
            tabela.addCell(produto.getNome());
            tabela.addCell(produto.getPreco() != null ? String.format("%.2f", produto.getPreco()) : "");
            tabela.addCell(produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem Categoria");
        }

        document.add(tabela);
        document.close();
    }


    // Primeiro exibe o formulário
    @GetMapping("/buscar-por-preco")
    public String buscarPorPrecoForm() {
        return "produtos/busca-preco"; // volta o formulário
    }

    // Depois trata o envio do formulário
    @GetMapping("/buscar-por-preco/resultados")
    public String buscarPorPrecoResultados(@RequestParam Double min, @RequestParam Double max, Model model) {
        model.addAttribute("produtos", produtoService.buscarPorPrecoEntre(min, max));
        return "produtos/busca-preco"; // mostra resultados na mesma página
    }



}
