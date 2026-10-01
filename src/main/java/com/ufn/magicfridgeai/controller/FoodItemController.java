package com.ufn.magicfridgeai.controller;

import com.ufn.magicfridgeai.model.FoodItem;
import com.ufn.magicfridgeai.service.FoodItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/food")
public class FoodItemController {

    private final FoodItemService service;

    public FoodItemController(FoodItemService service) {
        this.service = service;
    }

    //POST
    @PostMapping
    public ResponseEntity<FoodItem> criar(@RequestBody FoodItem food) {
        FoodItem salvo = service.salvar(food);
        return ResponseEntity.ok(salvo);
    }

    //GET
    @GetMapping
    public ResponseEntity<List<FoodItem>> listar() {
        List<FoodItem> lista = service.listar();
        return ResponseEntity.ok(lista);
    }

    //UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> atualizar(@PathVariable Long id, @RequestBody FoodItem food) {
        return service.atualizar(id, food)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();

    }
}
