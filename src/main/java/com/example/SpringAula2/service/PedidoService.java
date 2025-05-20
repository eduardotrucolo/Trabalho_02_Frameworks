package com.example.SpringAula2.service;

import com.example.SpringAula2.model.ItemCardapio;
import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Page<Pedido> listarPaginado(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido salvar(Pedido pedido) {
        if (pedido.getItens() != null) {
            double total = pedido.getItens().stream()
                    .mapToDouble(ItemCardapio::getPreco)
                    .sum();
            pedido.setValorTotal(total);
        }
        return pedidoRepository.save(pedido);
    }

    public void excluir(Long id) {
        pedidoRepository.deleteById(id);
    }

    public List<Pedido> buscarPorClienteEData(String cliente, LocalDate inicio, LocalDate fim) {
        return pedidoRepository.buscarPorClienteEData(cliente, inicio, fim);
    }
}


