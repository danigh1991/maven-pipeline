package com.core.common.util.validator;



import com.core.common.util.validator.annotation.NotNullStr;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class NotNullStrValidator implements ConstraintValidator<NotNullStr, String> {
    public void initialize(NotNullStr constraintAnnotation) {

    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.trim().equals("");
    }
}
