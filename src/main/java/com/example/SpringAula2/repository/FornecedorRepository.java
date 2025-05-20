package com.example.SpringAula2.repository;

import com.example.SpringAula2.dto.FornecedorProdutoCountDTO;
import com.example.SpringAula2.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    @Query(value = """
        SELECT f.nome AS nomeFornecedor, COUNT(pf.produto_id) AS quantidadeProdutos
        FROM produto_fornecedor pf
        JOIN fornecedor f ON pf.fornecedor_id = f.id
        GROUP BY f.nome
        ORDER BY f.nome
        """, nativeQuery = true)
    List<Object[]> contarProdutosPorFornecedorRaw();
}

