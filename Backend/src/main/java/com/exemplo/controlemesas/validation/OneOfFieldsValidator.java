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

		// If object is null, consider valid (use @NotNull on the type if you want to forbid null)
		if (value == null) return true;

		int filled = 0;

		for (String fieldName : fields) {
			try {
				Field f = findField(value.getClass(), fieldName);
				if (f == null) {
					// campo não existe – invalida DTO
					return false;
				}
				f.setAccessible(true);
				if (f.get(value) != null)
					filled++;
			} catch (Exception e) {
				// acesso inválido – invalida DTO
				return false;
			}
		}
		return filled == 1; // exatamente um preenchido
	}

	/**
	 * Busca um campo pelo nome na classe e nas superclasses.
	 */
	private Field findField(Class<?> clazz, String name) {
		Class<?> cur = clazz;
		while (cur != null && cur != Object.class) {
			try {
				return cur.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				cur = cur.getSuperclass();
			}
		}
		return null;
	}
}