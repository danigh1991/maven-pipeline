package com.core.common.util.validator.annotation;

import com.core.common.util.validator.NotNullStrValidator;
import com.core.common.util.validator.NotUrlInvalidCharValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = NotUrlInvalidCharValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotUrlInvalidChar {

    String message() default "Required Value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
