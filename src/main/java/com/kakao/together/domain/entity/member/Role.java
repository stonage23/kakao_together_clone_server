package com.kakao.together.domain.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

	GUEST("ROLE_GUEST"),
	MEMBER("ROLE_MEMBER"),
	ADMIN("ROLE_ADMIN");

	public String toAuthority() {
		return "ROLE_" + this.role;
	}

	private final String role;
}

