package com.auth.authservice.config;

import com.auth.authservice.service.JWTService;
import com.auth.authservice.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JWTService jwtService;

    public OAuth2SuccessHandler(@Lazy UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        // Process or create user
        userService.processOAuth2User(email, name, avatarUrl);

        // Generate JWT token
        String token = jwtService.generateToken(email);

        // Redirect to frontend with token
        String redirectUrl = "/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}