package com.barry.sec.service.impl;

import com.barry.sec.entities.AppRole;
import com.barry.sec.entities.AppUser;
import com.barry.sec.repositories.AppRoleRepository;
import com.barry.sec.repositories.AppUserRepository;
import com.barry.sec.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private final AppRoleRepository appRoleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = loadByUsername(username);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        appUser.getAppRoles().forEach(role-> authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }

    @Override
    public AppUser loadByUsername(String username){
        return appUserRepository.findByUsername(username)
                .orElseThrow(()-> new IllegalArgumentException(String.format("username %s not found",username)));
    }

    @Override
    public void addNewUser(AppUser appUser) {
        log.info("saving a new user {} to database", appUser.getUsername());
        String password = appUser.getPassword();
        appUser.setPassword(passwordEncoder.encode(password));
        appUserRepository.save(appUser);
    }

    @Override
    public void addNewRole(AppRole appRole) {
        log.info("saving a new role {} to database", appRole.getRoleName());
        appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role: {} to user: {}", roleName, username);
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        Optional<AppRole> appRole = appRoleRepository.findByRoleName(roleName);
        if (appRole.isPresent() && appUser.isPresent()) {
            appUser.get().getAppRoles().add(appRole.get());
        }
    }

    @Override
    public List<AppUser> listUsers() {
        log.info("fetching all users");
        return appUserRepository.findAll();
    }

}