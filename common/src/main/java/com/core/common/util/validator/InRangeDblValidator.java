package com.core.common.util.validator;

import com.core.common.util.validator.annotation.InRangeDbl;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InRangeDblValidator implements ConstraintValidator<InRangeDbl, Double> {
        private int min;
        private int max;

        @Override
        public void initialize(InRangeDbl constraintAnnotation) {
            this.min = constraintAnnotation.min();
            this.max = constraintAnnotation.max();
        }

        @Override
        public boolean isValid(Double value, ConstraintValidatorContext context) {
            return value == null || (value >= min && value <= max);
        }
    }