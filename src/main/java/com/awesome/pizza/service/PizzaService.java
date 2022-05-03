package com.awesome.pizza.service;

import com.awesome.pizza.model.Pizza;
import com.awesome.pizza.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PizzaService {

    @Autowired
    PizzaRepository pizzaRepository;

    public List<Pizza> getAll() {
        return pizzaRepository.findAll();
    }

    public Pizza getByUuid(String uuid) {
        Optional<Pizza> optionalPizza = pizzaRepository.findByUuid(uuid);
        return optionalPizza.orElse(null);
    }
}
