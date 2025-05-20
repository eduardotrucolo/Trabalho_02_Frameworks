package com.example.SpringAula2.repository;

import com.example.SpringAula2.dto.CategoriaProdutoCountDTO;
import com.example.SpringAula2.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT new com.example.SpringAula2.dto.CategoriaProdutoCountDTO(c.nome, COUNT(p)) " +
            "FROM Categoria c LEFT JOIN Produto p ON p.categoria = c " +
            "GROUP BY c.nome")
    List<CategoriaProdutoCountDTO> contarProdutosPorCategoria();

}
