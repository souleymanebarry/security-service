package com.barry.sec.service;

import com.barry.sec.entities.AppRole;
import com.barry.sec.entities.AppUser;

import java.util.List;

public interface AccountService {

    void addNewUser(AppUser appUser);

    void addNewRole(AppRole appRole);

    void addRoleToUser(String username, String roleName);

    AppUser loadByUsername(String username);

    List<AppUser> listUsers();
}
