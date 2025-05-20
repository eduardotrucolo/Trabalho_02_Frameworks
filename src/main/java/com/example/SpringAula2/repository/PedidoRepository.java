package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p WHERE (:cliente IS NULL OR p.cliente ILIKE %:cliente%) AND p.data BETWEEN :inicio AND :fim")
    List<Pedido> buscarPorClienteEData(@Param("cliente") String cliente,
                                       @Param("inicio") LocalDate inicio,
                                       @Param("fim") LocalDate fim);

    Page<Pedido> findAll(Pageable pageable); // paginação padrão
}


