package com.core.common.util.validator;



import com.core.common.util.validator.annotation.Ip;
import com.core.util.BaseUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class IpValidator implements ConstraintValidator<Ip, String> {
    public void initialize(Ip constraintAnnotation) {

    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return BaseUtils.isValidIp(value);
    }
}
