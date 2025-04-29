package com.auth.authservice.service;

import com.auth.authservice.model.User;
import com.auth.authservice.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    //@Autowired
    //private UserRepo repo;
    //@Autowired
    //private JWTService jwtService;
    //@Autowired
    //AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final UserRepo repo;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;

    @Autowired
    public UserService(UserRepo repo, JWTService jwtService, AuthenticationManager authManager) {
        this.repo = repo;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public User register(User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);

    }
    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public String verify(User user) {
         Authentication authentication=
                 authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
         if(authentication.isAuthenticated()){
             return jwtService.generateToken(user.getUsername());
         }else{
             return "Failed";
         }
    }
    public User processOAuth2User(String email, String name, String avatarUrl) {
        User user = findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setUsername(email); // Using email as username for OAuth2 users
            user.setGoogleAvatarUrl(avatarUrl);
            user.setRole("ROLE_USER");
            user.setTwoFactorEnabled(false);
            // Generate a random password for OAuth2 users
            user.setPassword(encoder.encode(UUID.randomUUID().toString()));
            return repo.save(user);
        }
        return user;
    }
}
