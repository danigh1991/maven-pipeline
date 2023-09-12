package com.core.common.util.validator.annotation;

import com.core.common.util.validator.NotHtmlAndXssValidator;
import com.core.common.util.validator.NotNullStrValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;




@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotHtmlAndXssValidator.class)
public @interface NotHtmlAndXss {

    String message() default "Invalid Value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
