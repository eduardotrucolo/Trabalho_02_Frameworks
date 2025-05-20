package com.example.SpringAula2.controller;

import com.example.SpringAula2.model.ItemCardapio;
import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.repository.ItemCardapioRepository;
import com.example.SpringAula2.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepo;
    private final ItemCardapioRepository itemRepo;

    @GetMapping
    public String listarPedidos(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String cliente,
                                @RequestParam(required = false) String inicio,
                                @RequestParam(required = false) String fim) {
        Page<Pedido> pedidos;

        if (cliente != null && !cliente.isBlank()) {
            pedidos = pedidoRepo.findByClienteContainingIgnoreCase(cliente, PageRequest.of(page, 5));
        } else if (inicio != null && fim != null) {
            LocalDate d1 = LocalDate.parse(inicio);
            LocalDate d2 = LocalDate.parse(fim);
            pedidos = pedidoRepo.findByDataBetween(d1, d2, PageRequest.of(page, 5));
        } else {
            pedidos = pedidoRepo.findAll(PageRequest.of(page, 5));
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("paginaAtual", page);
        return "pedido/lista";
    }

    @GetMapping("/novo")
    public String novoPedido(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("itens", itemRepo.findAll());
        return "pedido/form";
    }

    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute Pedido pedido, @RequestParam List<Long> itensSelecionados) {
        List<ItemCardapio> itens = itemRepo.findAllById(itensSelecionados);
        pedido.setItens(itens);
        pedido.setData(LocalDate.now());

        double total = itens.stream().mapToDouble(ItemCardapio::getPreco).sum();
        pedido.setValorTotal(total);

        pedidoRepo.save(pedido);
        return "redirect:/pedidos";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        pedidoRepo.deleteById(id);
        return "redirect:/pedidos";
    }
}

