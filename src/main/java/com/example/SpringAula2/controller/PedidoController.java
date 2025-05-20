package com.example.SpringAula2.controller;

import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.service.ItemCardapioService;
import com.example.SpringAula2.service.PedidoService;
import com.lowagie.text.*;
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
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ItemCardapioService itemCardapioService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("data").descending());
        Page<Pedido> pedidos = pedidoService.listarPaginado(pageable);
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        model.addAttribute("pedido", pedido);
        model.addAttribute("itens", itemCardapioService.listarTodos());
        return "pedidos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Pedido pedido) {
        pedidoService.salvar(pedido);
        return "redirect:/pedidos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        model.addAttribute("pedido", pedido);
        model.addAttribute("itens", itemCardapioService.listarTodos());
        return "pedidos/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
        return "redirect:/pedidos";
    }

    @GetMapping("/filtro")
    public String filtroForm() {
        return "pedidos/busca";
    }

    @GetMapping("/filtro/resultados")
    public String buscar(@RequestParam String cliente,
                         @RequestParam String inicio,
                         @RequestParam String fim,
                         Model model) {
        LocalDate dtInicio = LocalDate.parse(inicio);
        LocalDate dtFim = LocalDate.parse(fim);
        List<Pedido> pedidos = pedidoService.buscarPorClienteEData(cliente, dtInicio, dtFim);
        double total = pedidos.stream().mapToDouble(Pedido::getValorTotal).sum();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("total", total);
        return "pedidos/busca";
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(@RequestParam String inicio,
                            @RequestParam String fim,
                            HttpServletResponse response) throws IOException, DocumentException {
        LocalDate dtInicio = LocalDate.parse(inicio);
        LocalDate dtFim = LocalDate.parse(fim);
        List<Pedido> pedidos = pedidoService.buscarPorClienteEData("", dtInicio, dtFim);
        double total = pedidos.stream().mapToDouble(Pedido::getValorTotal).sum();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_pedidos.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("Relatório de Pedidos"));
        document.add(new Paragraph("De: " + inicio + " Até: " + fim));
        document.add(new Paragraph(" "));

        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);

        tabela.addCell("ID");
        tabela.addCell("Cliente");
        tabela.addCell("Data");
        tabela.addCell("Valor Total");

        for (Pedido p : pedidos) {
            tabela.addCell(p.getId().toString());
            tabela.addCell(p.getCliente());
            tabela.addCell(p.getData().toString());
            tabela.addCell(String.format("%.2f", p.getValorTotal()));
        }

        document.add(tabela);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total Geral: R$ " + String.format("%.2f", total)));
        document.close();
    }
}

