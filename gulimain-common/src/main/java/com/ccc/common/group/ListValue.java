package com.ccc.common.group;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({METHOD, FIELD,ANNOTATION_TYPE,CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)

public @interface ListValue {

    String message() default "{com.ccc.common.group.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] vals() default {};
}
