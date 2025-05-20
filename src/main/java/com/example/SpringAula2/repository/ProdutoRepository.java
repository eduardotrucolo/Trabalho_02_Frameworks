package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT p FROM Produto p WHERE p.preco BETWEEN :min AND :max")
    List<Produto> findByPrecoBetween(@Param("min") Double min, @Param("max") Double max);
}