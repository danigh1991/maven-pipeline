package com.core.common.util.validator;

import com.core.common.util.validator.annotation.NotUrlInvalidChar;
import com.core.util.BaseUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class NotUrlInvalidCharValidator implements ConstraintValidator<NotUrlInvalidChar, String> {
    public void initialize(NotUrlInvalidChar constraintAnnotation) {    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (BaseUtils.isStringSafeEmpty(value) || !BaseUtils.checkInvalidChar(value));
    }
}
