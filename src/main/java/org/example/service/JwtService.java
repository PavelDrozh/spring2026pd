package org.example.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateAccessToken(Authentication authentication);
}
