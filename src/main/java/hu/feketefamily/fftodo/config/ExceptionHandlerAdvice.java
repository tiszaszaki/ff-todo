package hu.feketefamily.fftodo.config;

import hu.feketefamily.fftodo.constants.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<String> handleConstraintViolation(HttpServletRequest request, ConstraintViolationException e) {
		return new ResponseEntity<>(ErrorMessages.CONSTRAINT_VIOLATION_MESSAGE, HttpStatus.BAD_REQUEST);
	}
}
