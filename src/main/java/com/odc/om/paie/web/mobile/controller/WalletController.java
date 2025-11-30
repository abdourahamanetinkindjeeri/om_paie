package com.odc.om.paie.web.mobile.controller;

import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.web.mobile.dto.TransactionRequestDTO;
import com.odc.om.paie.web.mobile.dto.TransactionResponseDTO;
import com.odc.om.paie.web.mobile.dto.WalletRequestDTO;
import com.odc.om.paie.web.mobile.dto.WalletResponseDTO;
import com.odc.om.paie.services.TransactionService;
import com.odc.om.paie.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.odc.om.paie.authenticated.Constants.APP_ROOT;

@RestController
@RequestMapping(APP_ROOT + "comptes")
@RequiredArgsConstructor
@Tag(name = "Gestion des comptes", description = "API pour la gestion des wallets et transactions")
public class WalletController {

    private final WalletService walletService;
    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Créer un nouveau wallet")
    public ResponseEntity<WalletResponseDTO> createWallet(@Valid @RequestBody WalletRequestDTO request) {
        WalletResponseDTO response = walletService.createWallet(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{numeroCompte}")
    @Operation(summary = "Obtenir les informations d'un compte")
    public ResponseEntity<WalletResponseDTO> getWallet(@PathVariable String numeroCompte) {
        UUID walletId = UUID.fromString(numeroCompte);
        WalletResponseDTO response = walletService.getWalletById(walletId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Lister les comptes de l'utilisateur connecté")
    public ResponseEntity<List<WalletResponseDTO>> getUserWallets(@AuthenticationPrincipal User user) {
        List<WalletResponseDTO> response = walletService.getWalletsByUser(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{numeroCompte}/balance")
    @Operation(summary = "Obtenir le solde d'un compte")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable String numeroCompte) {
        UUID walletId = UUID.fromString(numeroCompte);
        WalletResponseDTO wallet = walletService.getWalletById(walletId);
        return ResponseEntity.ok(wallet.getBalance());
    }

    @GetMapping("/{numeroCompte}/history")
    @Operation(summary = "Obtenir l'historique des transactions d'un compte")
    public ResponseEntity<List<TransactionResponseDTO>> getWalletHistory(@PathVariable String numeroCompte) {
        UUID walletId = UUID.fromString(numeroCompte);
        List<TransactionResponseDTO> response = transactionService.getTransactionsByWallet(walletId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{numeroCompte}/transfer")
    @Operation(summary = "Effectuer un transfert depuis ce compte")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @PathVariable String numeroCompte,
            @Valid @RequestBody TransactionRequestDTO request) {

        // Override the walletId from path parameter
        request.setWalletId(numeroCompte);
        request.setType("TRANSFER");

        TransactionResponseDTO response = transactionService.processTransaction(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{numeroCompte}/payment")
    @Operation(summary = "Effectuer un paiement depuis ce compte")
    public ResponseEntity<TransactionResponseDTO> payment(
            @PathVariable String numeroCompte,
            @Valid @RequestBody TransactionRequestDTO request) {

        // Override the walletId from path parameter
        request.setWalletId(numeroCompte);
        request.setType("WITHDRAWAL");

        TransactionResponseDTO response = transactionService.processTransaction(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{numeroCompte}/deposit")
    @Operation(summary = "Effectuer un dépôt sur ce compte")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @PathVariable String numeroCompte,
            @Valid @RequestBody TransactionRequestDTO request) {

        // Override the walletId from path parameter
        request.setWalletId(numeroCompte);
        request.setType("DEPOSIT");

        TransactionResponseDTO response = transactionService.processTransaction(request);
        return ResponseEntity.ok(response);
    }
}