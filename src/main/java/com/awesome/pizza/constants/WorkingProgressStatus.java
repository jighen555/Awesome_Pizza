package com.awesome.pizza.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkingProgressStatus {

    PENDING(1, "PENDING"),
    TAKE_CHARGE(2, "TAKE_CHARGE"),
    PROCESSED(3, "PROCESSED")
    ;

    public static final WorkingProgressStatus[] VALUES = values();

    private final Integer id;
    private final String description;

    public static WorkingProgressStatus getById(Integer id) {
        if (id != null) {
            if (id == 1)
                return PENDING;
            else if (id == 2)
                return TAKE_CHARGE;
            else if (id == 3)
                return PROCESSED;
            else
                return null;
        } else {
            return null;
        }
    }
}
