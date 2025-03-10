package com.example.Swp_Project.JwtUtils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
@Component
public class JwtUtils {

    private String secretKey = "6jSBqNjDF+HlVUMA5nOguNrWRqckFYfAPPgt3CpDOCo="; // Replace with your own secret key

    // Method to generate the JWT token (you may already have this in your setup)
    public String generateToken(String username, String email, UUID userID) {
        Claims claims = Jwts.claims().setSubject(username);  // Set the username as the subject of the token
        claims.put("email", email);
        claims.put("userID", userID.toString());  // Store userID as a string (can be UUID)



        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Set expiry time (1 day)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ Extract Email
    public String getEmailFromToken(String token) {
        return getClaims(token).get("email", String.class);
    }

    // ✅ Extract UserID
    public UUID getUserIDFromToken(String token) {
        return UUID.fromString(getClaims(token).get("userID", String.class));
    }




    // Method to validate the token (check if username matches and token is not expired)
    public boolean validateToken(String token) {
        try {
            // Parse the token to check its validity
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token); // This throws an exception if the token is invalid or expired

            // Optional: Check if the token has expired (you can also rely on ExpiredJwtException)
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                System.out.println("JWT expired");
                return false; // Explicitly return false if the token is expired
            }

            return true; // Valid token

        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid signature: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
        }
        return false; // Return false if any validation fails
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
