package com.core.common.util.validator;


import com.core.common.util.validator.annotation.NotHtmlAndXss;
import com.core.util.BaseUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class NotHtmlAndXssValidator implements ConstraintValidator<NotHtmlAndXss, String> {
    public void initialize(NotHtmlAndXss constraintAnnotation) {

    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(BaseUtils.hasHTML(value) || BaseUtils.hasHTML(value))
            return false;
        return true;
    }
}
