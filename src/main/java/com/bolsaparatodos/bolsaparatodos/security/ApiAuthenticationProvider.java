package com.bolsaparatodos.bolsaparatodos.security;

import com.bolsaparatodos.bolsaparatodos.entity.financial.User;
import com.bolsaparatodos.bolsaparatodos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApiAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    UserRepository userRepository;

    public ApiAuthenticationProvider(UserDetailsService userDetailsService) {
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getClass().equals(UsernamePasswordAuthenticationToken.class)) {
            return super.authenticate(authentication);
        } else if (authentication.getClass().equals(ApiKeyAuthenticationToken.class)) {
            Optional<User> userOptional = userRepository.findByApiKey((String) authentication.getPrincipal());

            User user = userOptional.orElseThrow(() -> new BadCredentialsException("Can't validate api key"));
            UserDetails userDetails = getUserDetailsService().loadUserByUsername(user.getEmail());

            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), userDetails.getPassword());

            return super.authenticate(newAuthentication);
        } else {
            return null;
        }
    }

    public boolean supports(Class<?> authentication) {
        return (ApiKeyAuthenticationToken.class.isAssignableFrom(authentication)) ||
                (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
