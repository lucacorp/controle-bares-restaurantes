package com.exemplo.controlemesas.exception;

import com.exemplo.controlemesas.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException ex) {
		ErrorResponse error = new ErrorResponse("Erro de validação: " + ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		ErrorResponse errorResponse = new ErrorResponse("Erro de validação");
		errorResponse.setDetails(errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse error = new ErrorResponse("Erro interno: " + ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
