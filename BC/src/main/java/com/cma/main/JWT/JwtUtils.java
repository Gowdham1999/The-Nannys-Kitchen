package com.cma.main.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service //Important
public class JwtUtils {

//    Please note that for JWT implementation we need JWT and Spring security dependencies

//    Dependencies Used For JWT:-

//        <dependency>
//            <groupId>org.springframework.boot</groupId>
//            <artifactId>spring-boot-starter-security</artifactId>
//        </dependency>
//
//        <dependency>
//            <groupId>org.springframework.security</groupId>
//            <artifactId>spring-security-test</artifactId>
//            <scope>test</scope>
//        </dependency>
//
//        <dependency>
//            <groupId>io.jsonwebtoken</groupId>
//            <artifactId>jjwt-api</artifactId>
//            <version>0.11.5</version>
//        </dependency>
//
//            <dependency>
//            <groupId>io.jsonwebtoken</groupId>
//            <artifactId>jjwt-impl</artifactId>
//            <version>0.11.5</version>
//            <scope>runtime</scope>
//        </dependency>
//
//        <dependency>
//            <groupId>io.jsonwebtoken</groupId>
//            <artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
//            <version>0.11.5</version>
//            <scope>runtime</scope>
//        </dependency>

    //    Step 1: Creating Secret key - It must be combination of upper, lower, numbers and special characters
    @Value("${secret_key}")
    private String secretKey;

    //    Step 2: The below two methods should be followed as is
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    //    Step 3: Getting username and expiration time from the token
    public String getUsernameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date getExpirationFromToken(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    //    Step 4: Checking whether the token is expired
    public boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    //    Step 5: Validating the token by checking the username and token expiration
    public boolean validateToken(String token, UserDetails userDetails) {
        return (getUsernameFromToken(token).equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //    Step 6: Function to create a token
    public String createToken(Map<String, Object> claims, String subject) {//subject also known as username
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))//Setting token issued time in milliseconds
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))//Setting expiration time in milliseconds. Here we are setting expiration time: current time + 1 hr in milliseconds
                .signWith(SignatureAlgorithm.HS256, secretKey)//Encryption algorithm
                .compact();
    }

    //    Step 7: Generating a token with role
    public String generateToken(String userName, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(claims, userName);


    }

}
