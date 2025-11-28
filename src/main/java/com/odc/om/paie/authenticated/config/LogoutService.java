package com.odc.om.paie.authenticated.config;


import com.odc.om.paie.authenticated.token.RefreshTokenRepository;
import com.odc.om.paie.authenticated.token.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }

        // Récupération du refresh token à partir des cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    String refreshTokenValue = cookie.getValue();

                    // Récupération du refresh token en base de données
                    var storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue).orElse(null);

                    // Invalidation du refresh token
                    if (storedRefreshToken != null) {
                        storedRefreshToken.setExpired(true);
                        storedRefreshToken.setRevoked(true);
                        refreshTokenRepository.save(storedRefreshToken);
                    }

                    // Suppression du cookie du refresh token
                    Cookie deleteCookie = new Cookie("refresh_token", null);
                    deleteCookie.setHttpOnly(true);
                    deleteCookie.setSecure(true);
                    deleteCookie.setPath("/");
                    deleteCookie.setMaxAge(0); // Supprime le cookie
                    response.addCookie(deleteCookie);
                }
            }
        }
    }


}
