package com.odc.om.paie.authenticated.auth;

import com.odc.om.paie.authenticated.auth.OtpService;
import com.odc.om.paie.authenticated.config.JwtService;
import com.odc.om.paie.authenticated.token.Token;
import com.odc.om.paie.authenticated.token.TokenRepository;
import com.odc.om.paie.authenticated.token.TokenType;
import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.authenticated.user.UserRepository;
import com.odc.om.paie.notification.model.Channel;
import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.usecase.SendNotificationUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final SendNotificationUseCase sendNotificationUseCase;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .pin(passwordEncoder.encode(request.getPin()))
                .telephone(request.getTelephone())
                .active(request.isActive())
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User validateCredentials(AuthenticationRequest request) {
        System.out.println("Tentative d'authentification pour téléphone: " + request.getTelephone());
        var user = repository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        System.out.println("Utilisateur trouvé: " + user.getTelephone());

        // Vérifier si le compte est bloqué
        if (user.isBlocked()) {
            throw new RuntimeException("Compte bloqué après 3 tentatives échouées");
        }

        // Vérifier le PIN
        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            // Incrémenter les tentatives échouées
            user.setFailedAttempts(user.getFailedAttempts() + 1);

            // Bloquer le compte après 3 tentatives
            if (user.getFailedAttempts() >= 3) {
                user.setBlocked(true);
            }

            repository.save(user);
            throw new RuntimeException("PIN incorrect. Tentatives restantes: " + (3 - user.getFailedAttempts()));
        }

        // Réinitialiser les tentatives échouées en cas de succès
        user.setFailedAttempts(0);
        repository.save(user);

        return user;
    }

    public AuthenticationResponse generateTokens(User user) {
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByTelephone(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Async
    public void sendOtpAsync(User user) {
        String otp = otpService.generateOtp(user);
        try {
            sendNotificationUseCase.execute(new NotificationRequest(
                    user.getEmail(),
                    "Code OTP de connexion",
                    "Votre code OTP est : " + otp + ". Valable 5 minutes.",
                    Channel.EMAIL
            ));
            log.info("OTP envoyé à {} pour l'utilisateur {}", user.getEmail(), user.getTelephone());
        } catch (Exception e) {
            log.error("Échec d'envoi de l'OTP à {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
