package com.example.mapper.validator;

import com.example.mapper.request.Message;

import jakarta.validation.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = AdMessageConstraintValidator.class)
@Documented
public @interface AdMessageConstraint {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class AdMessageConstraintValidator implements ConstraintValidator<AdMessageConstraint, Message> {
    private Validator validator;

    public AdMessageConstraintValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean isValid(Message value, ConstraintValidatorContext context) {
        if (value.isAd()) {
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