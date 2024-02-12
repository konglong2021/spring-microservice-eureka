package com.bronx.identityservice.service;

import com.bronx.identityservice.dto.AuthRequestDto;
import com.bronx.identityservice.entity.UserCredential;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private AuthenticationManager authenticationManager;

    public static final String secret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public JwtService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void validateToken(final String token){
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    public String generateToken(AuthRequestDto authRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(),authRequestDto.getPassword()));
        if (authentication.isAuthenticated()){
            Map<String,Object> claims = new HashMap<>();
            return createToken(claims,authRequestDto.getUsername());
        }
        throw new BadCredentialsException("Invalid access");
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hr
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}