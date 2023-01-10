package com.barry.sec.controllers;

import com.barry.sec.entities.AppRole;
import com.barry.sec.entities.AppUser;
import com.barry.sec.model.RoleToUserForm;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "Account service endpoint")
@RequestMapping("/api/v1")
public interface AccountController {

    @GetMapping("/users")
    ResponseEntity<List<AppUser>> getUsers();

    @PostMapping("/users")
    ResponseEntity<Void> saveUser(@RequestBody AppUser appUser);

    @PostMapping("/roles")
    ResponseEntity<Void> saveRole(@RequestBody AppRole appRole);

    @PostMapping("/addRoleToUser")
    ResponseEntity<Void> addRoleToUser(@RequestBody RoleToUserForm form);

    @GetMapping("/refreshToken")
    ResponseEntity<Void> getRefreshToken(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

}
