package com.awesome.pizza.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PizzaOrderConstant {

    NO_OLDEST_PENDING(1, "There are not oldest pending PizzaOrder object"),
    OLDEST_PENDING(2, "There are an oldest pending PizzaOrder object"),
    GOOD_TEST(3, "Good Test!"),
    PIZZA_ORDER_OK(4, "The PizzaOrder object is ok"),
    PIZZA_ORDER_NULL(5, "The PizzaOrder object is null"),
    ERROR_TAKING_CHARGE(6, "Error during taking charge"),
    SUCCESS_TAKING_CHARGE(7, "The order was successfully take charge"),
    ALREADY_TAKING_CHARGE(8, "The order has already been take charge"),
    ALREADY_PROCESSED(9, "The order has already been processed"),
    ERROR_PROCESSED(10, "Error during processed"),
    SUCCESS_PROCESSED(11, "The order was successfully processed"),
    STILL_PENDING(12, "The order is still pending"),
    ;

    public static final PizzaOrderConstant[] VALUES = values();

    private final Integer id;
    private final String description;
}
