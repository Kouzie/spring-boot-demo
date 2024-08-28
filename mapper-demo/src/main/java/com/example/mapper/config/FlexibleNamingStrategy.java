package com.example.mapper.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class FlexibleNamingStrategy extends PropertyNamingStrategy {
    @Override
    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
        if (defaultName.contains("_")) {
            return PropertyNamingStrategies.SNAKE_CASE.nameForField(config, field, defaultName);
        } else {
            return PropertyNamingStrategies.LOWER_CAMEL_CASE.nameForField(config, field, defaultName);
        }
    }

    @Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return nameForField(config, null, defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return nameForField(config, null, defaultName);
    }
}
