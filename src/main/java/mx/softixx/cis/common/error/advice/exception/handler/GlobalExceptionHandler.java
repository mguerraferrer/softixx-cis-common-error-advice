package mx.softixx.cis.common.error.advice.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ValidationException;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import mx.softixx.cis.common.validation.exception.CustomException;
import mx.softixx.cis.common.validation.util.ProblemDetailUtils;

/**
 * @author Maikel Guerra Ferrer - mguerraferrer@gmail.com
 *
 */
@ControllerAdvice
@Slf4j(topic = "GlobalExceptionHandler")
public class GlobalExceptionHandler {

	/**
	 * Catch for all the exception that are not handled by other exception handlers
	 * 
	 * @param ex
	 * @return ProblemDetail with INTERNAL_SERVER_ERROR status
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ProblemDetail handleInternalServerError(Exception ex) {
		log.error("#handleInternalServerError error: {}", ex.getMessage());
		return ProblemDetailUtils.internalServerError();
	}
	
	/**
	 * Catch for all validation exception
	 * 
	 * @param ex {@link CustomException}
	 * @return ProblemDetail with BAD_REQUEST status
	 */
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleConstraintViolationException(ValidationException e) {
		if (e.getCause() != null) {
			try {
				CustomException cex = (CustomException) e.getCause();
				if (cex != null) {
					val properties = CustomException.populateProperties(cex);
					return ProblemDetailUtils.badRequest(cex.getMessage(), properties);
				}
			} catch (Exception ex) {
				log.error("#handleConstraintViolationException error: {}", ex.getMessage());
			}
		}
		return ProblemDetailUtils.badRequest(e.getLocalizedMessage());
	}

}