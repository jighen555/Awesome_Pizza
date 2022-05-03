package com.awesome.pizza.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultStatus {

    OK(1, "OK"),
    KO(1, "KO")
    ;

    public static final ResultStatus[] VALUES = values();

    private final Integer id;
    private final String description;
}
