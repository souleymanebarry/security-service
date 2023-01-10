package com.barry.sec.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.stream.Collectors;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;


@Slf4j
public final class MappingUtils {

    public static final String BEARER = "Bearer ";

    private MappingUtils() { }

    static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    public static byte[] getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return hmacShaKeyFor(keyBytes).getEncoded();
    }

    public static String getAccessToken(HttpServletRequest request, User user, Algorithm algorithm) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() +60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority:: getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);
    }

    public static String getRefreshToken(HttpServletRequest request, User user, Algorithm algorithm) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() +15*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    public static String writeJson(Serializable obj) {
        ObjectMapper mapper = new ObjectMapper();
        String json=  "";
        try {
            json += " "+mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            log.trace("object is not json compatible",e);
        }
        return json;

    }

}
