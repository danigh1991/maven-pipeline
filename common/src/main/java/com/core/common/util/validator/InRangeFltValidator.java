package com.core.common.util.validator;

import com.core.common.util.validator.annotation.InRangeFlt;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InRangeFltValidator implements ConstraintValidator<InRangeFlt, Float> {

        private int min;
        private int max;

        @Override
        public void initialize(InRangeFlt constraintAnnotation) {
            this.min = constraintAnnotation.min();
            this.max = constraintAnnotation.max();
        }

        @Override
        public boolean isValid(Float value, ConstraintValidatorContext context) {
            return value == null || (value >= min && value <= max);
        }
    }