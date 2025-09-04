package com.kakao.together.exception;

import lombok.Getter;

/**
 * 예외 공통 응답처리를 위한 커스텀 예외 클래스. <br>
 * - CustomException으로 throw한 경우 CustomException으로 다시 감싸지 않는다.
 * - 직접 생성한 예외 클래스를 catch 할 경우 CustomException(Throwable cause) 을 throw
 */
@Getter
public class CustomException extends RuntimeException {

	/**
	 * 기본 응답 메시지, 응답 상태 코드
	 */
	private ErrorCode errorCode;

	/**
	 * 디버깅용 메시지와 500 status 응답
	 * @param message
	 */
	public CustomException(String message) {
		super(message);
		this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
	}

	/**
	 * 사전에 정의해둔 ErrorCode으로 응답하는 경우
	 * @param errorCode
	 */
	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	/**
	 * 사전에 정의해둔 ErrorCode로 응답. 디버깅용 메시지
	 * @param errorCode
	 * @param message
	 */
	public CustomException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * 발생한 예외 추적. 500 상태 코드로 응답
	 * @param cause CustomException으로 래핑한 예외
	 */
	public CustomException(Throwable cause) {
		super(cause.getMessage(), cause);
		this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
	}

	/**
	 * 발생한 예외 추적 + 디버깅 메시지. 500 상태 코드로 응답
	 * @param cause
	 * @param message
	 */
	public CustomException(Throwable cause, String message) {
		super(message, cause);
		this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
	}
}
