package com.exemplo.controlemesas.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class OneOfFieldsValidator implements ConstraintValidator<OneOfFields, Object> {

	private String[] fields;

	@Override
	public void initialize(OneOfFields annotation) {
		this.fields = annotation.fields();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext ctx) {

		int filled = 0;

		for (String fieldName : fields) {
			try {
				Field f = value.getClass().getDeclaredField(fieldName);
				f.setAccessible(true);
				if (f.get(value) != null)
					filled++;
			} catch (Exception e) {
				// campo não existe ou acesso inválido – invalida DTO
				return false;
			}
		}
		return filled == 1; // ✅ exatamente um preenchido
	}
}
