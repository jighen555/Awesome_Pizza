package com.awesome.pizza.service;

import com.awesome.pizza.model.PizzaOrder;
import com.awesome.pizza.repository.PizzaOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PizzaOrderService {

    @Autowired
    PizzaOrderRepository pizzaOrderRepository;

    public List<PizzaOrder> getAll() {
        return pizzaOrderRepository.findAll();
    }

    public PizzaOrder getByUuid(String uuid) {
        Optional<PizzaOrder> optionalWorkingProgress = pizzaOrderRepository.findByUuid(uuid);
        return optionalWorkingProgress.orElse(null);
    }

    public PizzaOrder getByIdentifier(String identifier) {
        Optional<PizzaOrder> optionalWorkingProgress = pizzaOrderRepository.findByIdentifier(identifier);
        return optionalWorkingProgress.orElse(null);
    }

    /**
     * Method to check the oldest order pending
     * @return The oldest order pending
     */
    public PizzaOrder getOldPending() {
        List<PizzaOrder> pizzaOrders = pizzaOrderRepository.findOldPending();
        if (pizzaOrders != null && !pizzaOrders.isEmpty()) {
            return pizzaOrders.get(0);
        } else {
            return null;
        }
    }

    public boolean save(PizzaOrder pizzaOrder) {
        try {
            pizzaOrderRepository.save(pizzaOrder);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean delete(PizzaOrder pizzaOrder) {
        try {
            pizzaOrderRepository.delete(pizzaOrder);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
