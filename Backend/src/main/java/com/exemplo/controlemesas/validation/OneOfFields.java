package com.exemplo.controlemesas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OneOfFieldsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneOfFields {

	/** Campos que participam da regra “um‑ou‑outro” */
	String[] fields();

	String message() default "Informe apenas um dos campos: {fields}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
