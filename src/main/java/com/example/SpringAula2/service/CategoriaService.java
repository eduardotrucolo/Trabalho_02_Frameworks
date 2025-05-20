package com.example.SpringAula2.service;

import com.example.SpringAula2.dto.CategoriaProdutoCountDTO;
import com.example.SpringAula2.model.Categoria;
import com.example.SpringAula2.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaProdutoCountDTO> contarProdutosPorCategoria() {
        return categoriaRepository.contarProdutosPorCategoria();
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria salvar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void deletar(Long id) {
        categoriaRepository.deleteById(id);
    }

    public Categoria atualizar(Long id, Categoria novaCategoria) {
        return categoriaRepository.findById(id).map(categoriaExistente -> {
            categoriaExistente.setNome(novaCategoria.getNome());
            return categoriaRepository.save(categoriaExistente);
        }).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

}
