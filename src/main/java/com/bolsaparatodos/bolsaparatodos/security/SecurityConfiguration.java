package com.bolsaparatodos.bolsaparatodos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    ApiAuthenticationProvider apiAuthenticationProvider;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(apiAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        ApiKeyAuthFilter filter = new ApiKeyAuthFilter();
        filter.setAuthenticationManager(authenticationManager());

        http
            .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
            .cors()
                .and()
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/*")
                .authenticated()
                .and()
            .addFilterAt(filter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Component
    class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(
                HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException authException) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
