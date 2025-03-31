package com.example.Swp_Project.JwtUtils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
@Component
public class JwtUtils {

    private String secretKey = "6jSBqNjDF+HlVUMA5nOguNrWRqckFYfAPPgt3CpDOCo=";
    private final SecureRandom secureRandom=new SecureRandom();

    public String generateToken(String username, String email, UUID userID,String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("email", email);
        claims.put("userID", userID.toString());
        claims.put("role",role);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Set expiry time (1 day)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[32]; // 32 bytes = 256 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes); // Base64 URL-encoded, ~43 characters
    }


    public String getUsernameFromToken(String token) {
         return getClaims(token).getSubject();
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).get("email", String.class);
    }

    public UUID getUserIDFromToken(String token) {
        return UUID.fromString(getClaims(token).get("userID", String.class));
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            Date expiration = claims.getExpiration();

            if (expiration.before(new Date())) {
                System.out.println("JWT expired");
                return false;
            }
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid signature: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
