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
import java.time.format.DateTimeFormatter; // Importe esta classe
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ItemCardapioService itemCardapioService;

    // Defina o formatter como uma constante para reuso
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    public String buscar(@RequestParam(required = false) String cliente,
                         @RequestParam(required = false) String inicio,
                         @RequestParam(required = false) String fim,
                         Model model) {
        try {
            List<Pedido> pedidos;
            double total = 0.0;

            model.addAttribute("clienteParam", cliente != null ? cliente : "");
            model.addAttribute("inicioParam", inicio != null ? inicio : "");
            model.addAttribute("fimParam", fim != null ? fim : "");

            boolean filtroPorClienteAtivo = (cliente != null && !cliente.isBlank());
            boolean filtroPorDataAtivo = (inicio != null && !inicio.isBlank() && fim != null && !fim.isBlank());

            if (!filtroPorClienteAtivo && !filtroPorDataAtivo) {
                model.addAttribute("erro", "Informe o nome do cliente ou um intervalo de datas válido.");
                return "pedidos/busca";
            }

            if (filtroPorClienteAtivo) {
                pedidos = pedidoService.buscarPorCliente(cliente);
            } else if (filtroPorDataAtivo) {
                // Aqui, LocalDate.parse(string, formatter) com "yyyy-MM-dd"
                LocalDate dtInicio = LocalDate.parse(inicio, DATE_FORMATTER);
                LocalDate dtFim = LocalDate.parse(fim, DATE_FORMATTER);
                pedidos = pedidoService.buscarPorData(dtInicio, dtFim);
            } else {
                pedidos = Collections.emptyList();
                model.addAttribute("erro", "Nenhum critério de filtro válido fornecido.");
                return "pedidos/busca";
            }

            total = pedidos.stream().mapToDouble(Pedido::getValorTotal).sum();

            model.addAttribute("pedidos", pedidos);
            model.addAttribute("total", total);
        } catch (DateTimeParseException e) {
            // Atualize a mensagem de erro para o formato esperado pelo input type="date"
            model.addAttribute("erro", "Datas inválidas. Formato esperado: AAAA-MM-DD.");
            model.addAttribute("clienteParam", cliente != null ? cliente : "");
            model.addAttribute("inicioParam", inicio != null ? inicio : "");
            model.addAttribute("fimParam", fim != null ? fim : "");
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao buscar pedidos: " + e.getMessage());
            model.addAttribute("clienteParam", cliente != null ? cliente : "");
            model.addAttribute("inicioParam", inicio != null ? inicio : "");
            model.addAttribute("fimParam", fim != null ? fim : "");
        }
        return "pedidos/busca";
    }


    @GetMapping("/exportar-pdf")
    public void exportarPdf(@RequestParam(required = false) String cliente,
                            @RequestParam(required = false) String inicio,
                            @RequestParam(required = false) String fim,
                            HttpServletResponse response) throws IOException, DocumentException {
        List<Pedido> pedidos;
        double total;

        try {
            boolean filtroPorClienteAtivo = (cliente != null && !cliente.isBlank());
            boolean filtroPorDataAtivo = (inicio != null && !inicio.isBlank() && fim != null && !fim.isBlank());

            if (filtroPorClienteAtivo) {
                pedidos = pedidoService.buscarPorCliente(cliente);
            } else if (filtroPorDataAtivo) {
                // Aqui, LocalDate.parse(string, formatter) com "yyyy-MM-dd"
                LocalDate dtInicio = LocalDate.parse(inicio, DATE_FORMATTER);
                LocalDate dtFim = LocalDate.parse(fim, DATE_FORMATTER);
                pedidos = pedidoService.buscarPorData(dtInicio, dtFim);
            } else {
                pedidos = Collections.emptyList();
            }

            total = pedidos.stream().mapToDouble(Pedido::getValorTotal).sum();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=relatorio_pedidos.pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            document.add(new Paragraph("Relatório de Pedidos"));
            if (filtroPorClienteAtivo) {
                document.add(new Paragraph("Cliente: " + cliente));
            } else if (filtroPorDataAtivo) {
                document.add(new Paragraph("De: " + inicio + " Até: " + fim)); // Pode exibir YYYY-MM-DD ou formatar para exibição DD/MM/YYYY
            } else {
                document.add(new Paragraph("Filtro: Nenhum critério de filtro válido para o relatório."));
            }
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
                // Formatar a data para exibição no PDF para o formato brasileiro
                tabela.addCell(p.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                tabela.addCell(String.format("R$ %.2f", p.getValorTotal()));
            }

            document.add(tabela);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Geral: R$ " + String.format("%.2f", total)));

            document.close();

        } catch (DateTimeParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datas inválidas para exportação. Formato esperado: AAAA-MM-DD.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao gerar PDF: " + e.getMessage());
        }
    }
}