package com.odc.om.paie.authenticated.auditing;

import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.authenticated.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Primary
@Component
@Qualifier("entityAuditor")
public class EntityAuditor implements AuditorAware<User> {

    private final UserService userService;

    public EntityAuditor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        // Retourne un Optional qui enveloppe un utilisateur connecté, ou null si non connecté
        return Optional.ofNullable(userService.getUtilisateurConnecte());
    }
}
