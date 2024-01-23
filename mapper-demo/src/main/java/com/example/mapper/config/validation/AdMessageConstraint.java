package com.example.mapper.config.validation;

import com.example.mapper.request.validation.Ad;
import com.example.mapper.request.validation.Message;

import jakarta.validation.*;
import java.lang.annotation.*;
import java.util.Set;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdMessageConstraintValidator.class)
@Documented
public @interface AdMessageConstraint {
    String message() default "invalid param in ad condition";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class AdMessageConstraintValidator implements ConstraintValidator<AdMessageConstraint, Message> {
    private Validator validator;

    // 생성자 초기화 필수
    public AdMessageConstraintValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean isValid(Message value, ConstraintValidatorContext context) {
        if (value.getIsAd()) {
            // 위반한 검증 목록
            final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(value, Ad.class);
            if (constraintViolations != null && constraintViolations.size() != 0) {
                // 기본 메시지 제거하고 새로운 MethodArgumentNotValidException 에 message 에 정의된 문자열를 넣는 과정
                context.disableDefaultConstraintViolation();
                constraintViolations.stream()
                        .forEach(constraintViolation -> context
                                .buildConstraintViolationWithTemplate(constraintViolation.getMessageTemplate())
                                .addPropertyNode(constraintViolation.getPropertyPath().toString())
                                .addConstraintViolation());
                return false;
            }
        }
        return true;
    }
}