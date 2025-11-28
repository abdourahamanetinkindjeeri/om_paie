package com.odc.om.paie.authenticated.auth;


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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")

//    public ResponseEntity<AuthenticationResponse> authenticate(
//            @RequestBody AuthenticationRequest request
//    ) {
//        return ResponseEntity.ok(service.authenticate(request));
//    }
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        try {
            // Appeler la méthode login qui renvoie à la fois l'access token, refresh token et l'utilisateur
            AuthenticationResponse authResponse = service.authenticate(request);

            // Ajouter le refresh token comme cookie HttpOnly
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                    .httpOnly(true)           // Empêcher l'accès en JavaScript
                    .secure(true)             // S'assurer que le cookie est envoyé uniquement via HTTPS TODO : true
                    .path("/")                // Disponible sur tout le site
                    .maxAge(100 * 24 * 60 * 60) // Durée de validité de 100 jours
                    .sameSite("Strict")       // Renforcer la politique de sécurité SameSite
                    .build();

            // Ajouter l'en-tête Set-Cookie avec le refresh token
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            // Retourner la réponse avec l'access token dans le corps
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getAccessToken())
                    .body(authResponse);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la connexion : l'email ou le mot de passe est incorrect.");
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }


}
