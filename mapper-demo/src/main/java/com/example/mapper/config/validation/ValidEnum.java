package com.example.mapper.config.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValidator.class})
public @interface ValidEnum {
    String message() default "invalid param for enum";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum> target();
}


class EnumValidator implements ConstraintValidator<ValidEnum, String> {
    Enum[] enumValues;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        enumValues = constraintAnnotation.target().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return Arrays.stream(enumValues).filter(it ->
                it.name().equals(value)
        ).findAny().isPresent();
    }
}
