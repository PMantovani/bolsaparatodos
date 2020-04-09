package com.bolsaparatodos.bolsaparatodos.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ApiKeyAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public ApiKeyAuthenticationToken(String apiKey) {
        super(apiKey, "");
    }
}
