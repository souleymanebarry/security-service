package com.barry.sec.controllers.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.barry.sec.controllers.AccountController;
import com.barry.sec.entities.AppRole;
import com.barry.sec.entities.AppUser;
import com.barry.sec.model.RoleToUserForm;
import com.barry.sec.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.barry.sec.utils.MappingUtils.BEARER;
import static com.barry.sec.utils.MappingUtils.getSignInKey;
import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;


@RestController
@Slf4j
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    public AccountControllerImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok(accountService.listUsers());
    }

    @Override
    public ResponseEntity<Void> saveUser(AppUser appUser) {
        accountService.addNewUser(appUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> saveRole(AppRole appRole) {
        accountService.addNewRole(appRole);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> addRoleToUser(RoleToUserForm form) {
        accountService.addRoleToUser(form.getUsername(), form.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> getRefreshToken(HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {

        String authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationToken != null && authorizationToken.startsWith(BEARER)){

            try {
                String refreshToken = authorizationToken.substring(BEARER.length());
                Algorithm algorithm = Algorithm.HMAC256(getSignInKey());

                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                AppUser appUser = accountService.loadByUsername(username);

                String accessToken = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() +60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",appUser.getAppRoles().stream()
                                .map(AppRole:: getRoleName)
                                .collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> idToken = new HashMap<>();

                idToken.put("access-token", accessToken);
                idToken.put("refresh-token", refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),idToken);

            }catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader(ERROR_MESSAGE,e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = Map.of(ERROR_MESSAGE, e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
                throw e;
            }

        }
        else {
            throw new IllegalStateException("Refresh token is missing");
        }

        return ResponseEntity.ok().build();
    }


}
