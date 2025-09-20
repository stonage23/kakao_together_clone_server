package com.kakao.together.security;

import com.kakao.together.domain.entity.member.MemberStatus;
import com.kakao.together.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 이메일 계정 로그인은 이메일로 CustomUserDetails 객체 생성
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) {

        return this.memberRepository.findByEmail(username)
                .map(member -> {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toAuthority()));
                            return new CustomUserDetails(
                                    String.valueOf(member.getId())
                                    , member.getPassword()
                                    , member.getId()
                                    , member.getEmail()
                                    , authorities
                                    , (member.getMemberStatus() != MemberStatus.LOCKED)
                                    , (member.getMemberStatus() == MemberStatus.ACTIVE));
                        }
                ).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}