package com.kakao.together.security;

import com.kakao.together.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return this.memberRepository.findByEmail(username)
                .map(member ->
                        User.builder()
                                .username(member.getEmail())
                                .password(member.getPassword())
                                .authorities(new SimpleGrantedAuthority(member.getAuthority().getRole()))
                                .build()
                ).orElseThrow(
                        () -> new UsernameNotFoundException("사용자를 찾을수 없습니다.")
                );
    }
}