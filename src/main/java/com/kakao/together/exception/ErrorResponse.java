package com.kakao.together.exception;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 예외처리 결과 클라이언트에 반환될 ResponseEntity의 응답 본문(body)을 생성하는 클래스
 */
@Getter
public class ErrorResponse {

	private String message;
	private int status;
	private String code;
	private Map<String, String> errors;

	private ErrorResponse(final ErrorCode code) {
		this.message = code.getMessage();
		this.status = code.getHttpStatus().value();
		this.code = code.getCode();
		this.errors = new HashMap<>();
	}

	private ErrorResponse(final ErrorCode code, final Map<String, String> errors) {
		this.message = code.getMessage();
		this.status = code.getHttpStatus().value();
		this.code = code.getCode();
		this.errors = errors;
	}

	private ErrorResponse(final ErrorCode code, String message) {
		this.message = message;
		this.status = code.getHttpStatus().value();
		this.code = code.getCode();
		this.errors = new HashMap<>();
	}

	public static ErrorResponse of(final ErrorCode code) {
		return new ErrorResponse(code);
	}

	public static ErrorResponse of(ErrorCode code, BindingResult bindingResult) {
		return new ErrorResponse(code, buildErrors(bindingResult));
	}

	public static ErrorResponse of(ErrorCode code, Set<ConstraintViolation<?>> constraintViolation) {
		return new ErrorResponse(code, buildErrors(constraintViolation));
	}

	public static ErrorResponse of(final CustomException e) {
		if (e.getDetailMessage() != null) return new ErrorResponse(e.getErrorCode(), e.getDetailMessage());
		else return new ErrorResponse(e.getErrorCode());
	}

	/**
	 * Valid 유효성 검사 실패 Error을 반환하기 전 처리
	 * @param bindingResult
	 * @return
	 */
	private static Map<String, String> buildErrors(BindingResult bindingResult) {
		Map<String , String > errors= new HashMap<>();
		bindingResult.getAllErrors().forEach((error)->{
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});
		return errors;
	}

	/**
	 * Validated 유효성 검사 실패 Error을 반환하기 전 처리
	 * @param constraintViolation
	 * @return
	 */
	private static Map<String, String> buildErrors(Set<ConstraintViolation<?>> constraintViolation) {
		Map<String , String > errors= new HashMap<>();
		constraintViolation.forEach((error)->{
			String propertyPath = String.valueOf(error.getPropertyPath());
			String fieldName = propertyPath.substring(propertyPath.indexOf('.')+1, propertyPath.length());
			String message = error.getMessage();
			errors.put(fieldName, message);
		});
		return errors;
	}
}
