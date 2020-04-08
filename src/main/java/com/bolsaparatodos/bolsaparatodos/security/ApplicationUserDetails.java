package com.bolsaparatodos.bolsaparatodos.security;

import com.bolsaparatodos.bolsaparatodos.entity.financial.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class ApplicationUserDetails implements UserDetails {

    private long id;
    private String email;
    private String password;
    private boolean isAdmin;

    public ApplicationUserDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.id = user.getId();
        this.isAdmin = user.isAdmin();
    }

    public long getId() {
        return this.id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return isAdmin ?
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")) :
                Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
