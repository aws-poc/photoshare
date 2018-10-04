package com.aws.photosharing.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;

@Slf4j
@Component
public class JwtHelper {
    private KeyPair keyPair;

    public JwtHelper(@Autowired KeyPairReteriver keyPairReteriver) {
        this.keyPair = keyPairReteriver.getKeyPair();
    }

    public String create(String userName) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + 3600000);
        String jws = Jwts.builder()
                .setIssuer("ps")
                .setSubject(userName)
                .setAudience("ps")
                .setExpiration(expiration)
                .setIssuedAt(issuedAt)
                .signWith(SignatureAlgorithm.RS256, this.keyPair.getPrivate())
                .compact();
        return jws;
    }

    public String getUserName(String authorization) {
        Claims claims = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(authorization).getBody();
        return claims.getSubject();
    }
}
