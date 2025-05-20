package com.example.SpringAula2.service;

import com.example.SpringAula2.dto.FornecedorProdutoCountDTO;
import com.example.SpringAula2.model.Fornecedor;
import com.example.SpringAula2.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public List<FornecedorProdutoCountDTO> contarProdutosPorFornecedor() {
        List<Object[]> results = fornecedorRepository.contarProdutosPorFornecedorRaw();
        return results.stream()
                .map(obj -> new FornecedorProdutoCountDTO(
                        (String) obj[0],
                        ((Number) obj[1]).longValue()
                ))
                .collect(Collectors.toList());
    }


    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    public Fornecedor salvar(Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    public Fornecedor atualizar(Long id, Fornecedor novoFornecedor) {
        return fornecedorRepository.findById(id).map(fornecedorExistente -> {
            fornecedorExistente.setNome(novoFornecedor.getNome());
            fornecedorExistente.setCnpj(novoFornecedor.getCnpj());
            return fornecedorRepository.save(fornecedorExistente);
        }).orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    public void deletar(Long id) {
        fornecedorRepository.deleteById(id);
    }
}
