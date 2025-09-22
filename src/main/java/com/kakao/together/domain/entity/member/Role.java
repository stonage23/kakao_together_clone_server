package com.kakao.together.domain.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

	ANONYMOUS("ANONYMOUS"),
	MEMBER("MEMBER"),
	ADMIN("ADMIN");

	public String toAuthority() {
		return "ROLE_" + this.role;
	}

	private final String role;
}

