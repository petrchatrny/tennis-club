package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Service for generating ang validating JsonWebTokens.
 *
 * <p>
 * It can generate token from claims and extract claims from token.
 * It also signs tokens with secret key.
 * </p>
 *
 * @see JwtConfig
 */
@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    /**
     * @param jwtConfig configuration of json web tokens like expiration date etc
     */
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * Method generates new JWT.
     *
     * @param claims      extra information added to token
     * @param phoneNumber user's unique phoneNumber for identification
     * @return JWT
     */
    public String generateToken(Map<String, Object> claims, String phoneNumber) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // date of token creation
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration()))
                .setSubject(phoneNumber)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts user's phone number from token.
     *
     * @param token JWT
     * @return user's phone number
     */
    public String getUserPhoneNumber(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Checks if expiration date of taken is lower than current date
     *
     * @param token JWT
     * @return if token is expired
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build();

        return parser.parseClaimsJws(token).getBody();
    }
}
