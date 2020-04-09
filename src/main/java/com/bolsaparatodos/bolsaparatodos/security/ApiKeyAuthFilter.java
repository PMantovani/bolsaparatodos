package com.bolsaparatodos.bolsaparatodos.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

public class ApiKeyAuthFilter extends AbstractAuthenticationProcessingFilter {

    ApiKeyAuthFilter() {
        super("/**");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        Authentication authResult;
        try {
            authResult = attemptAuthentication(request, response);
            if (authResult == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (AuthenticationException failed) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(request, response);// return to others spring security filters
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        Authentication authentication = this.getAuthenticationToken(authorizationHeader);
        return getAuthenticationManager().authenticate(authentication);
    }

    private Authentication getAuthenticationToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new BadCredentialsException("No authentication header provided");
        } else if (authorizationHeader.startsWith("Basic ")) {
            String principal = this.getAuthenticationProperty(authorizationHeader, true);
            String credentials = this.getAuthenticationProperty(authorizationHeader, false);
            return new UsernamePasswordAuthenticationToken(principal, credentials);
        } else if (authorizationHeader.startsWith("Bearer ")) {
            return new ApiKeyAuthenticationToken(authorizationHeader.replace("Bearer ", ""));
        } else {
            throw new BadCredentialsException("No authentication header provided");
        }
    }

    private String getAuthenticationProperty(String authorizationHeader, boolean fetchPrincipal) {
        byte[] decodedBytes = Base64.getDecoder().decode(authorizationHeader.replace("Basic ", ""));
        String decodedString = new String(decodedBytes);
        String principal = decodedString.split(":")[0];
        String credentials = decodedString.split(":").length > 1 ? decodedString.split(":")[1] : null;

        return fetchPrincipal ? principal : credentials;
    }
}