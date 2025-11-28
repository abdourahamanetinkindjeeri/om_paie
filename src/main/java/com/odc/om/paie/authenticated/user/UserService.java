package com.odc.om.paie.authenticated.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private PasswordEncoder passwordEncoder;


    public User getUtilisateurConnecte() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (User) authentication.getPrincipal();  // ou autre logique pour obtenir l'utilisateur
        }
        return null;
    }


}
