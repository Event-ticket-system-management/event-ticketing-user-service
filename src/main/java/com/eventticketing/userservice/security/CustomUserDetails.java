package com.eventticketing.userservice.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername(){
        return this.email;
    }

}
