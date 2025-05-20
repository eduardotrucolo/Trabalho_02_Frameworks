package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByClienteContainingIgnoreCase(String cliente, Pageable pageable);

    Page<Pedido> findByDataBetween(LocalDate inicio, LocalDate fim, Pageable pageable);

}

