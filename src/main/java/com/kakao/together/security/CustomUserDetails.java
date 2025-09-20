package com.kakao.together.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Long userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonLocked;
    private final boolean enabled;

    public CustomUserDetails(String username, Collection<? extends GrantedAuthority> authorities) {
        this(username, "", Long.valueOf(username), "", authorities, true, true);
    }

    public CustomUserDetails(String username, String password, Long userId, String email, Collection<? extends GrantedAuthority> authorities, boolean accountNonLocked, boolean enabled) {
        this.username = username;
        this.password = password;
        this.userId = userId;
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
        return accountNonLocked;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getEmail() {
        return this.email;
    }
}
