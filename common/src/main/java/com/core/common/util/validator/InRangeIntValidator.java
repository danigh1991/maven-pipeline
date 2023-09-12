package com.core.common.util.validator;

import com.core.common.util.validator.annotation.InRangeInt;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InRangeIntValidator implements ConstraintValidator<InRangeInt, Integer> {

    private int min;
    private int max;

    @Override
    public void initialize(InRangeInt constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || (value >= min && value <= max);
    }
}
