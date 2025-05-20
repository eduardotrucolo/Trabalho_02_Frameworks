package com.example.SpringAula2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private Double preco;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    public enum Categoria {
        BEBIDA, LANCHE, SOBREMESA
    }
}
