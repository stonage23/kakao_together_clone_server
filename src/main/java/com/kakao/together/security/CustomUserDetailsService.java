package com.kakao.together.security;

import com.kakao.together.domain.entity.member.MemberStatus;
import com.kakao.together.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return this.memberRepository.findByEmail(username)
                .map(member -> {
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority(member.getRole().toAuthority()));
                            return new CustomUserDetails(
                                    member.getEmail()
                                    , member.getPassword()
                                    , member.getId()
                                    , member.getEmail()
                                    , authorities
                                    , (member.getMemberStatus() != MemberStatus.LOCKED)
                                    , (member.getMemberStatus() == MemberStatus.ACTIVE));
                        }
                ).orElseThrow(
                        () -> new UsernameNotFoundException("사용자를 찾을수 없습니다.")
                );
    }
}