package com.example.SpringAula2.service;


import com.example.SpringAula2.model.Categoria;
import com.example.SpringAula2.model.Fornecedor;
import com.example.SpringAula2.model.Produto;
import com.example.SpringAula2.repository.CategoriaRepository;
import com.example.SpringAula2.repository.FornecedorRepository;
import com.example.SpringAula2.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private FornecedorRepository fornecedorRepository;

    public Produto salvar(Produto produto) {
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(produto.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + produto.getCategoria().getId()));
            produto.setCategoria(categoria);
        } else {
            produto.setCategoria(null);
        }

        if (produto.getFornecedores() != null && !produto.getFornecedores().isEmpty()) {
            List<Fornecedor> fornecedoresCompletos = produto.getFornecedores().stream()
                    .map(f -> fornecedorRepository.findById(f.getId())
                            .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado: " + f.getId())))
                    .collect(Collectors.toList());
            produto.setFornecedores(fornecedoresCompletos);
        } else {
            produto.setFornecedores(null);
        }

        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto novoProduto) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            produtoExistente.setNome(novoProduto.getNome());
            produtoExistente.setPreco(novoProduto.getPreco());

            if (novoProduto.getCategoria() != null && novoProduto.getCategoria().getId() != null) {
                Categoria categoria = categoriaRepository.findById(novoProduto.getCategoria().getId())
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + novoProduto.getCategoria().getId()));
                produtoExistente.setCategoria(categoria);
            } else {
                produtoExistente.setCategoria(null);
            }

            if (novoProduto.getFornecedores() != null && !novoProduto.getFornecedores().isEmpty()) {
                List<Fornecedor> fornecedores = novoProduto.getFornecedores().stream()
                        .map(f -> fornecedorRepository.findById(f.getId())
                                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado: " + f.getId())))
                        .collect(Collectors.toList());
                produtoExistente.setFornecedores(fornecedores);
            } else {
                produtoExistente.setFornecedores(null);
            }

            return produtoRepository.save(produtoExistente);
        }).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Page<Produto> listarPaginado(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    public List<Produto> buscarPorPrecoEntre(Double min, Double max) {
        return produtoRepository.findByPrecoBetween(min, max);
    }

}
