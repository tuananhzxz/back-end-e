package com.shop.ecommerce.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

public class UpdateUtils {
    public static void updateNonNullFields(Object target, Object source) {
        Set<Object> visited = new HashSet<>();
        updateNonNullFields(target, source, visited);
    }

    public static void updateNonNullFields(Object target, Object source, Set<Object> visited) {
        if (visited.contains(source)) {
            return;
        }
        visited.add(source);

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        Set<String> excludeFields = new HashSet<>(Arrays.asList("category", "category2", "category3"));

        for (Field sourceField : sourceClass.getDeclaredFields()) {
            if (excludeFields.contains(sourceField.getName())) {
                continue;
            }
            try {
                Field targetField = targetClass.getDeclaredField(sourceField.getName());
                sourceField.setAccessible(true);
                targetField.setAccessible(true);

                Object value = sourceField.get(source);
                if (value != null) {
                    if (isNestedObject(sourceField)) {
                        Object targetNestedObject = targetField.get(target);
                        if (targetNestedObject == null) {
                            targetNestedObject = targetField.getType().getDeclaredConstructor().newInstance();
                            targetField.set(target, targetNestedObject);
                        }
                        updateNonNullFields(targetNestedObject, value, visited);
                    } else if (targetField.getType().isAssignableFrom(sourceField.getType())) {
                        targetField.set(target, value);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | InstantiationException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to update field", e);
            }
        }
    }

    private static boolean isNestedObject(Field field) {
        Class<?> type = field.getType();
        return !type.isPrimitive()
                && !type.equals(String.class)
                && !Collection.class.isAssignableFrom(type)
                && !Map.class.isAssignableFrom(type)
                && !type.equals(Integer.class)
                && !type.equals(Long.class)
                && !type.equals(Double.class)
                && !type.equals(Float.class)
                && !type.equals(Boolean.class)
                && !type.equals(LocalDate.class);
    }
}