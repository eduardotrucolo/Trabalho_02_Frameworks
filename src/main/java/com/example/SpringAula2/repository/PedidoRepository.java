package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens")
    Page<Pedido> findAllWithItens(Pageable pageable);

    List<Pedido> findByClienteContainingIgnoreCase(String cliente);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.data BETWEEN :inicio AND :fim")
    List<Pedido> findByDataBetweenWithItens(LocalDate inicio, LocalDate fim);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens WHERE LOWER(p.cliente) LIKE LOWER(CONCAT('%', :cliente, '%'))")
    List<Pedido> findByClienteContainingIgnoreCaseWithItens(String cliente);
}


