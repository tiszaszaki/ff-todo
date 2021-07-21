package hu.feketefamily.fftodo.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.exception.NotExistException;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	@ExceptionHandler({ConstraintViolationException.class, DataIntegrityViolationException.class})
	public ResponseEntity<String> handleConstraintViolation(HttpServletRequest request, Exception e) {
		return new ResponseEntity<>(ErrorMessages.CONSTRAINT_VIOLATION_MESSAGE, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({NotExistException.class})
	public ResponseEntity<String> handleNotExist(HttpServletRequest request, NotExistException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
