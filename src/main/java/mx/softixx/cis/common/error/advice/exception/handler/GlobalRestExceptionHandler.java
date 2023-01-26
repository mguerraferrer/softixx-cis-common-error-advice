package mx.softixx.cis.common.error.advice.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import mx.softixx.cis.common.validation.util.ProblemDetailUtils;

/**
 * @author Maikel Guerra Ferrer - mguerraferrer@gmail.com
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j(topic = "GlobalRestExceptionHandler")
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		val problemDetail = ProblemDetailUtils.badRequest(ex.getBindingResult());
		return new ResponseEntity<>(problemDetail, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.error("#handleHttpMessageNotReadable error: {}", ex.getMessage());
		log.error("#handleHttpMessageNotReadable cause: {}", ex.getMostSpecificCause().getMessage());

		val problemDetail = ProblemDetailUtils.badRequest();
		return new ResponseEntity<>(problemDetail, headers, status);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
		return ProblemDetailUtils.badRequest(e.getConstraintViolations());
	}

}