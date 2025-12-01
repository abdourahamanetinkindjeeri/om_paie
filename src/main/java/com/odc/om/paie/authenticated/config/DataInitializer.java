package com.odc.om.paie.authenticated.config;

import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.authenticated.user.UserRepository;
import com.odc.om.paie.entities.Transaction;
import com.odc.om.paie.entities.TransactionStatus;
import com.odc.om.paie.entities.TransactionType;
import com.odc.om.paie.entities.Wallet;
import com.odc.om.paie.repositories.TransactionRepository;
import com.odc.om.paie.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Création des utilisateurs de test...");

            // Utilisateur ADMIN
            User admin = User.builder()
                    .firstname("Admin")
                    .lastname("System")
                    .email("abdourahamanetinkindjeeri99@gmail.com")
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

            // Création des données de test (wallets et transactions)
            createTestData(employee);
            createTestData(manager);
            createTestData(admin);

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

    private void createTestData(User employee) {
        log.info("Création des données de test pour l'utilisateur EMPLOYEE...");

        // Créer un wallet principal pour l'employé
        Wallet mainWallet = Wallet.builder()
                .user(employee)
                .balance(new BigDecimal("1000.00"))
                .currency("XOF")
                .isMain(true)
                .build();
        walletRepository.save(mainWallet);
        log.info("Wallet principal créé pour EMPLOYEE avec solde: {}", mainWallet.getBalance());

        // Créer un wallet secondaire
        Wallet secondaryWallet = Wallet.builder()
                .user(employee)
                .balance(new BigDecimal("500.00"))
                .currency("XOF")
                .isMain(false)
                .build();
        walletRepository.save(secondaryWallet);
        log.info("Wallet secondaire créé pour EMPLOYEE avec solde: {}", secondaryWallet.getBalance());

        // Créer quelques transactions
        Transaction deposit = Transaction.builder()
                .wallet(mainWallet)
                .amount(new BigDecimal("1000.00"))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .reference("DEP-" + System.currentTimeMillis())
                .meta("{\"description\":\"Dépôt initial\"}")
                .build();
        transactionRepository.save(deposit);

        Transaction withdrawal = Transaction.builder()
                .wallet(mainWallet)
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.COMPLETED)
                .reference("WTH-" + System.currentTimeMillis())
                .meta("{\"description\":\"Retrait test\"}")
                .build();
        transactionRepository.save(withdrawal);

        Transaction transfer = Transaction.builder()
                .wallet(mainWallet)
                .destinationWallet(secondaryWallet)
                .amount(new BigDecimal("300.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .reference("TRF-" + System.currentTimeMillis())
                .meta("{\"description\":\"Transfert vers wallet secondaire\",\"destinationWalletId\":\"" + secondaryWallet.getId() + "\"}")
                .build();
        transactionRepository.save(transfer);

        // Mettre à jour les soldes
        mainWallet.setBalance(new BigDecimal("500.00")); // 1000 - 200 - 300 = 500
        secondaryWallet.setBalance(new BigDecimal("800.00")); // 500 + 300 = 800
        walletRepository.save(mainWallet);
        walletRepository.save(secondaryWallet);

        log.info("Données de test créées avec succès!");
    }
}