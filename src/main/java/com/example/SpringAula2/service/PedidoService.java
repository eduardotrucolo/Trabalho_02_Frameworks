package com.example.SpringAula2.service;

import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Page<Pedido> listarPaginado(Pageable pageable) {
        return pedidoRepository.findAllWithItens(pageable);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        double total = pedido.getItens().stream()
                .mapToDouble(item -> item.getPreco())
                .sum();
        pedido.setValorTotal(total);
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public void excluir(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(String cliente) {
        return pedidoRepository.findByClienteContainingIgnoreCaseWithItens(cliente);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorData(LocalDate inicio, LocalDate fim) {
        return pedidoRepository.findByDataBetweenWithItens(inicio, fim);
    }
}

