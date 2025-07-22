package com.kakao.together.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CustomException extends RuntimeException {

	private String message = "";
	private ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}

	public CustomException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.message = message;
	}
}
