package com.odc.om.paie.authenticated.token;


import com.odc.om.paie.authenticated.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(UUID id);

    Optional<Token> findByToken(String token);

    // Requête pour récupérer l'utilisateur associé à un jeton d'accès spécifique
    @Query("SELECT t.user FROM Token t WHERE t.token = :token")
    Optional<User> findUserByToken(String token);

    // Méthode pour supprimer tous les access tokens d'un utilisateur
    void deleteByUser(User user);
}
