package com.kakao.together.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	/**
	 * 응답 메시지
	 */
	private String message;
	/**
	 * 기본 응답 메시지, 응답 상태 코드
	 */
	private ErrorCode errorCode;
	/**
	 * 디버깅 로깅용 Throwable
	 */
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
	 * 발생한 예외 추적이 필요한 경우. 500 상태 코드로 응답
	 * @param cause CustomException으로 래핑한 예외
	 */
	public CustomException(Throwable cause) {
		super(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
		this.cause = cause;
		this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
	}

	/**
	 * 발생한 예외 추적 + 메시지 로깅. 500 상태 코드로 응답
	 * @param cause
	 * @param message
	 */
	public CustomException(Throwable cause, String message) {
		super(message);
		this.cause = cause;
		this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
	}
}
