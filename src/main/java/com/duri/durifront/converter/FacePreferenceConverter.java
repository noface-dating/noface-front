package com.duri.durifront.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class FacePreferenceConverter implements AttributeConverter<List<Integer>, String> {

    private static final String DELIMITER = "";
    private static final int PREF_SIZE = 10;

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "0".repeat(PREF_SIZE);
        }
        return attribute.stream()
                .map(i -> i == null ? "0" : String.valueOf(i == 1 ? 1 : 0))
                .limit(PREF_SIZE)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        List<Integer> list = new ArrayList<>();
        for (char c : dbData.toCharArray()) {
            list.add(c == '1' ? 1 : 0);
        }
        while (list.size() < PREF_SIZE) {
            list.add(0);
        }
        return list.subList(0, PREF_SIZE);
    }
}
