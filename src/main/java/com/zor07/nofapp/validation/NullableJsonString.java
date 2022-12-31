package com.zor07.nofapp.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NullableJsonStringValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableJsonString {
    String message() default "The String is not in JSON format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}