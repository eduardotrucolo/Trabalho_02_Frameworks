package com.example.SpringAula2.controller;

import com.example.SpringAula2.model.ItemCardapio;
import com.example.SpringAula2.repository.ItemCardapioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cardapio")
@RequiredArgsConstructor
public class ItemCardapioController {

    private final ItemCardapioRepository itemRepo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("itens", itemRepo.findAll());
        return "cardapio/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("item", new ItemCardapio());
        return "cardapio/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ItemCardapio item) {
        itemRepo.save(item);
        return "redirect:/cardapio";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemRepo.findById(id).orElseThrow());
        return "cardapio/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        itemRepo.deleteById(id);
        return "redirect:/cardapio";
    }
}

