package com.ufn.magicfridgeai.service;

import com.ufn.magicfridgeai.model.FoodItem;
import com.ufn.magicfridgeai.repository.FoodItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FoodItemService {

    private final FoodItemRepository repository;
    public FoodItemService(FoodItemRepository repository) {
        this.repository = repository;
    }

    public FoodItem salvar(FoodItem foodItem) {
        return repository.save(foodItem);
    }

    public List<FoodItem> listar() {
        return repository.findAll();
    }

    //Listar por ID
    public Optional<FoodItem> buscarPorId(Long id) {
        return repository.findById(id);
    }
    //Atualizar
    public Optional<FoodItem> atualizar(Long id, FoodItem foodItem) {
        return repository.findById(id)
                .map(foodExistente -> {
                    foodExistente.setNome(foodItem.getNome());
                    foodExistente.setCategoria(foodItem.getCategoria());
                    foodExistente.setQuantidade(foodItem.getQuantidade());
                    foodExistente.setValidade(foodItem.getValidade());
                    return repository.save(foodExistente);
                });
    }

    //Deletar
    public void deletar(Long id) {
        repository.deleteById(id);
    }

}
