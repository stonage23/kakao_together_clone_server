package com.kakao.together.domain.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authority {

	GUEST("ROLE_GUEST"),
	MEMBER("ROLE_MEMBER"),
	ADMIN("ROLE_ADMIN");

	private final String role;
}

