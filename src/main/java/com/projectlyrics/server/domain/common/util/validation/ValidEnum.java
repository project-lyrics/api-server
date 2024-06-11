package com.projectlyrics.server.domain.common.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {

  String message() default "Invalid value. This is not permitted.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  Class<? extends Enum<?>> enumClass();
}
