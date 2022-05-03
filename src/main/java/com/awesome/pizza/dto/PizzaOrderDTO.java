package com.awesome.pizza.dto;

import com.awesome.pizza.model.Pizza;
import com.awesome.pizza.model.PizzaOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PizzaOrderDTO {

    private PizzaOrder pizzaOrder;
    private List<Pizza> pizzas;

    public static PizzaOrderDTO build(PizzaOrder pizzaOrder, List<Pizza> pizzas) {
        return PizzaOrderDTO
                .builder()
                .pizzaOrder(pizzaOrder)
                .pizzas(pizzas)
                .build();
    }
}
