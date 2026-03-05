package org.example.service.impl;

import org.example.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-ttl-minutes}")
    private long accessTtlMinutes;

    public JwtServiceImpl(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateAccessToken(Authentication authentication) {
        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(accessTtlMinutes, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
