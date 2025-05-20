package com.example.SpringAula2.dto;


public class CategoriaProdutoCountDTO {
    private String nomeCategoria;
    private Long quantidadeProdutos;

    public CategoriaProdutoCountDTO(String nomeCategoria, Long quantidadeProdutos) {
        this.nomeCategoria = nomeCategoria;
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public Long getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    public void setQuantidadeProdutos(Long quantidadeProdutos) {
        this.quantidadeProdutos = quantidadeProdutos;
    }
}
