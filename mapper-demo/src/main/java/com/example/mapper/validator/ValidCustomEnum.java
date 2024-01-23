package com.example.mapper.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Documented // java doc 에 포함
@Constraint(validatedBy = CustomEnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCustomEnum {
    String message() default "is invalid enum type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class CustomEnumValidator implements ConstraintValidator<ValidCustomEnum, String> {
    private List<String> enumNames;

    @Override
    public void initialize(ValidCustomEnum constraintAnnotation) {
        enumNames = Arrays.stream(CustomEnum.values())
                .map(customEnum -> customEnum.name())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // null 값은 에러
        }
        for (String enumName : enumNames) {
            if (enumName.equals(value)) {
                return true; // 입력된 값이 Enum에 존재하는 경우 유효
            }
        }
        return false; // 입력된 값이 Enum에 존재하지 않는 경우 유효하지 않음
    }
}