package dev.m4nd3l.chatting4ever.api;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static io.jsonwebtoken.io.Decoders.BASE64URL;

public class JWTTokenProvider {
    private static final String SECRET_STRING = "89a38907ee304a8ef5311f5f65aefc2700d64dd02091dc8722b34ad377ce86aa";
    private static final SecretKey cryptographicKey = Keys.hmacShaKeyFor(BASE64URL.decode(SECRET_STRING));

    public static String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(cryptographicKey)
                .compact();
    }

    public static String validateTokenAndGetUsername(String token) {
        return Jwts.parser()
                .verifyWith(cryptographicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
