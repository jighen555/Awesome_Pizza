package com.awesome.pizza.converter;

import com.awesome.pizza.constants.WorkingProgressStatus;

import javax.persistence.AttributeConverter;

public class WorkingProgressStatusConverter implements AttributeConverter<WorkingProgressStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WorkingProgressStatus workingProgressStatus) {
        if (workingProgressStatus != null)
            return workingProgressStatus.getId();
        else
            return null;
    }

    @Override
    public WorkingProgressStatus convertToEntityAttribute(Integer id) {
        return WorkingProgressStatus.getById(id);
    }
}
