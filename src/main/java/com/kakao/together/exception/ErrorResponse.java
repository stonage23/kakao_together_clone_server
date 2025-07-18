package com.kakao.together.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 예외처리 결과 클라이언트에 반환될 ResponseEntity의 응답 본문(body)을 생성하는 클래스
 */
@Getter
public class ErrorResponse {

	private String message;
	private int status;
	private String code;
	private Map<String, String> errors;

	private ErrorResponse(final ErrorCode code, final String message) {
		this.message = message;
		this.status = code.getHttpStatus().value();
		this.code = code.getCode();
		this.errors = new HashMap<>();
	}

	private ErrorResponse(final ErrorCode code) {
		this.message = code.getMessage();
		this.status = code.getHttpStatus().value();
		this.code = code.getCode();
		this.errors = new HashMap<>();
	}

	public static ErrorResponse of(final ErrorCode code) {
		return new ErrorResponse(code);
	}

	public static ErrorResponse of(final ErrorCode code, final String message) { return new ErrorResponse(code, message); }
}
