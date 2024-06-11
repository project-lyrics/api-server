package com.projectlyrics.server.domain.common.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  private Class<? extends Enum<?>> enumClass;

  @Override
  public void initialize(ValidEnum constraintAnnotation) {
    this.enumClass = constraintAnnotation.enumClass();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return Arrays.stream(enumClass.getEnumConstants())
        .anyMatch(e -> e.name().equals(value));
  }
}
