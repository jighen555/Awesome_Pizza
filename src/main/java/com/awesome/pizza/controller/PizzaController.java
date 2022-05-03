package com.awesome.pizza.controller;

import com.awesome.pizza.model.Pizza;
import com.awesome.pizza.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pizza")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<Pizza> pizzas = pizzaService.getAll();

            if (pizzas.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                Map<String, List<Pizza>> map = new LinkedHashMap<>();
                map.put("Pizzas", pizzas);
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Pizza> getByUuid(@PathVariable("uuid") String uuid) {
        try {
            if (uuid == null || uuid.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

            Pizza pizza = pizzaService.getByUuid(uuid);

            if (pizza == null)
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            else
                return new ResponseEntity<>(pizza, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
