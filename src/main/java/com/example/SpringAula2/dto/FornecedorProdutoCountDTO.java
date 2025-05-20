package com.example.SpringAula2.dto;

public class FornecedorProdutoCountDTO {
    private String nomeFornecedor;
    private Long quantidadeProdutos;

    public FornecedorProdutoCountDTO(String nomeFornecedor, Long quantidadeProdutos) {
        this.nomeFornecedor = nomeFornecedor;
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public Long getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    public void setQuantidadeProdutos(Long quantidadeProdutos) {
        this.quantidadeProdutos = quantidadeProdutos;
    }
}
