package com.kakao.together.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Long id;
    private final String email;
    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonLocked;
    private final boolean enabled;

    public CustomUserDetails(String username, String password, Long memberId, String email, Set<GrantedAuthority> authorities) {
        this(username, password, memberId, email, authorities, true, true);
    }

    public CustomUserDetails(String username, String password, Long id, String email, Set<GrantedAuthority> authorities, boolean accountNonLocked, boolean enabled) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.email = email;
        this.authorities = authorities;
        this.accountNonLocked = accountNonLocked;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked();
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled();
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }
}
