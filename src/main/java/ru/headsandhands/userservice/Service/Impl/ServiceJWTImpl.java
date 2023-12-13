package ru.headsandhands.userservice.Service.Impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.headsandhands.userservice.Model.User;
import ru.headsandhands.userservice.Service.ServiceJWT;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class ServiceJWTImpl implements ServiceJWT {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshExpiration;

     @Override
     public String extractUserName(String token){
         return extractClaim(token, Claims::getSubject);
     }

     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
         final Claims claims = extractAllClaims(token);
         return claimsResolver.apply(claims);
     }

     public String generateToken(User user){
         return generateToken(new HashMap<>(), user);
     }

     public String generateToken(
             Map<String, Object> extraClaims,
             User user
     ){
        return buildToken(extraClaims, user, jwtExpiration);
     }
    public String generateRefreshToken(
            User user
    ){
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

     public String buildToken(
             Map<String, Object> extraClaims,
             User user,
             Long expiration
     ){

         return Jwts
                 .builder()
                 .setClaims(extraClaims)
                 .setId(String.valueOf(user.getId()))
                 .setSubject(user.getUsername())
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + expiration))
                 .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                 .compact();
     }

     public boolean isTokenValid(String token, UserDetails userDetails){
         final String userName = extractUserName(token);
         return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
     }

    private boolean isTokenExpired(String token) {
         return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
         return extractClaim(token, Claims::getExpiration);
    }

    public String extractId(String token){
         return (extractClaim(token, Claims::getId));
    }

    public Claims extractAllClaims(String token){
         return Jwts
                 .parserBuilder()
                 .setSigningKey(getSignInKey())
                 .build()
                 .parseClaimsJws(token)
                 .getBody();
    }

    public Key getSignInKey() {
         byte[] keyBytes = Decoders.BASE64.decode(secretKey);
         return Keys.hmacShaKeyFor(keyBytes);
    }

}
