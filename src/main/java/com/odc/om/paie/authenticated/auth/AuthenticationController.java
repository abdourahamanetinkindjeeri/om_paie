package com.odc.om.paie.authenticated.auth;

import com.odc.om.paie.authenticated.auth.OtpService;
import com.odc.om.paie.authenticated.auth.OtpVerificationRequest;

import com.odc.om.paie.authenticated.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import static com.odc.om.paie.authenticated.Constants.APP_ROOT;



@RestController
@RequestMapping(APP_ROOT + "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final OtpService otpService;
    private final UserRepository userRepository;


    @PostMapping("/login")

//    public ResponseEntity<AuthenticationResponse> authenticate(
//            @RequestBody AuthenticationRequest request
//    ) {
//        return ResponseEntity.ok(service.authenticate(request));
//    }
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        try {
            // Valider les credentials
            var user = service.validateCredentials(request);

            // Si l'utilisateur a un email, envoyer OTP
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                service.sendOtpAsync(user);
                return ResponseEntity.ok("OTP envoyé à votre email. Veuillez vérifier.");
            } else {
                // Si pas d'email, retourner directement les tokens
                AuthenticationResponse authResponse = service.generateTokens(user);

                // Ajouter le refresh token comme cookie HttpOnly
                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(100 * 24 * 60 * 60)
                        .sameSite("Strict")
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getAccessToken())
                        .body(authResponse);
            }

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la connexion : le téléphone ou le PIN est incorrect.");
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request, HttpServletResponse response) {
        boolean isValid = otpService.validateOtp(request.telephone(), request.otp());
        if (isValid) {
            // Clear OTP after successful verification
            otpService.clearOtpByTelephone(request.telephone());

            // Générer les tokens
            var user = userRepository.findByTelephone(request.telephone()).orElseThrow();
            AuthenticationResponse authResponse = service.generateTokens(user);

            // Ajouter le refresh token comme cookie HttpOnly
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(100 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getAccessToken())
                    .body(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP invalide ou expiré");
        }
    }


}
