package com.kakao.together.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CustomException extends RuntimeException {

	private String message;
	private ErrorCode errorCode;
	private Throwable cause;

	/**
	 * 예외를 던지는 곳에서 작성한 메시지와 500 상태 반환
	 * @param message
	 */
	public CustomException(String message) {
		super(message);
		this.message = message;
		this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
	}

	/**
	 * 사전에 정의해둔 ErrorCode으로 예외를 처리하는 경우
	 * @param errorCode
	 */
	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}

	/**
	 * 사전에 정의해둔 ErrorCode에 반환할 메시지만 변경하는 경우
	 * @param errorCode
	 * @param message
	 */
	public CustomException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.message = message;
	}

	/**
	 * 발생한 예외 추적이 필요한 경우
	 * @param message
	 * @param cause
	 */
	public CustomException(String message, Throwable cause) {
		super(message, cause);
		this.cause = cause;
		this.message = message;
		this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
	}
}
