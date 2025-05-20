package com.example.SpringAula2.service;

import com.example.SpringAula2.model.ItemCardapio;
import com.example.SpringAula2.repository.ItemCardapioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemCardapioService {

    @Autowired
    private ItemCardapioRepository repository;

    public List<ItemCardapio> listarTodos() {
        return repository.findAll();
    }

    public Optional<ItemCardapio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ItemCardapio salvar(ItemCardapio item) {
        return repository.save(item);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<ItemCardapio> buscarPorPrecoEntre(Double min, Double max) {
        return repository.findByPrecoBetween(min, max);
    }
}
