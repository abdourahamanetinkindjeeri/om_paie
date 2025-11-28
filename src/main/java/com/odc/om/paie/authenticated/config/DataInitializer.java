package com.odc.om.paie.authenticated.config;

import com.odc.om.paie.authenticated.user.Role;
import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.authenticated.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Création des utilisateurs de test...");

            // Utilisateur ADMIN
            User admin = User.builder()
                    .firstname("Admin")
                    .lastname("System")
                    .email("dev.testghost@gmail.com")
                    .pin(passwordEncoder.encode("1234"))
                    .telephone("771234567")
                    .active(true)
                    .isBlocked(false)
                    .failedAttempts(0)
                    .build();
            userRepository.save(admin);
            log.info("Utilisateur ADMIN créé - Téléphone: {}, PIN: 1234", admin.getTelephone());

            // Utilisateur MANAGER
            User manager = User.builder()
                    .firstname("Manager")
                    .lastname("Test")
                    .email("manager@paie.com")
                    .pin(passwordEncoder.encode("5678"))
                    .telephone("772345678")
                    .active(true)
                    .isBlocked(false)
                    .failedAttempts(0)
                    .build();
            userRepository.save(manager);
            log.info("Utilisateur MANAGER créé - Téléphone: {}, PIN: 5678", manager.getTelephone());

            // Utilisateur EMPLOYE
            User employee = User.builder()
                    .firstname("Employee")
                    .lastname("Test")
                    .email("employee@paie.com")
                    .pin(passwordEncoder.encode("9999"))
                    .telephone("773456789")
                    .active(true)
                    .isBlocked(false)
                    .failedAttempts(0)
                    .build();
            userRepository.save(employee);
            log.info("Utilisateur EMPLOYE créé - Téléphone: {}, PIN: 9999", employee.getTelephone());

            // Utilisateur avec compte bloqué pour test
            User blockedUser = User.builder()
                    .firstname("Blocked")
                    .lastname("User")
                    .email("blocked@paie.com")
                    .pin(passwordEncoder.encode("0000"))
                    .telephone("774567890")
                    .active(true)
                    .isBlocked(true)
                    .failedAttempts(3)
                    .build();
            userRepository.save(blockedUser);
            log.info("Utilisateur BLOQUÉ créé - Téléphone: {} (compte bloqué)", blockedUser.getTelephone());

            log.info("Utilisateurs de test créés avec succès!");
            log.info("=== INFORMATIONS DE CONNEXION ===");
            log.info("ADMIN: Téléphone=771234567, PIN=1234");
            log.info("MANAGER: Téléphone=772345678, PIN=5678");
            log.info("EMPLOYEE: Téléphone=773456789, PIN=9999");
            log.info("BLOQUÉ: Téléphone=774567890 (compte bloqué)");
            log.info("==================================");
        } else {
            log.info("Des utilisateurs existent déjà, pas de création d'utilisateurs de test.");
        }
    }
}