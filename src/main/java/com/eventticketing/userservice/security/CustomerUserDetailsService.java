package com.eventticketing.userservice.security;

import com.eventticketing.userservice.entity.User;
import com.eventticketing.userservice.exception.UsernameNotFoundException;
import com.eventticketing.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
       User user = userRepository.findByEmail(username)
                  .orElseThrow(() -> {
                    log.warn("User authentication failed. Username not found: {}", username);
                     return new UsernameNotFoundException("User not found with username!");
                });


        List<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
