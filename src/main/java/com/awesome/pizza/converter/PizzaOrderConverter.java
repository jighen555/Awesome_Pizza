package com.awesome.pizza.converter;

import javax.persistence.AttributeConverter;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class PizzaOrderConverter implements AttributeConverter<Set<String>, String> {
    private final String GROUP_DELIMITER = ";";

    @Override
    public String convertToDatabaseColumn(Set<String> stringList) {
        if (stringList == null) {
            return "";
        }
        return String.join(GROUP_DELIMITER, stringList);
    }

    @Override
    public Set<String> convertToEntityAttribute(String string) {
        Set<String> resultingSet = new HashSet<>();
        StringTokenizer st = new StringTokenizer(string, GROUP_DELIMITER);
        while (st.hasMoreTokens())
            resultingSet.add(st.nextToken());
        return resultingSet;
    }
}
