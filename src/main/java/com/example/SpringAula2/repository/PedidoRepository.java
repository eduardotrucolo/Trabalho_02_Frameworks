package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteContainingIgnoreCase(String cliente);

    List<Pedido> findByDataBetween(LocalDate inicio, LocalDate fim);

}


