package com.core.common.util.validator.annotation;

import com.core.common.util.validator.IpValidator;
import com.core.common.util.validator.NotNullStrValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = IpValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Ip {

    String message() default "Required Value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
