package com.auth.authservice.controller;

import com.auth.authservice.model.User;
import com.auth.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService service;


    @PostMapping("/register")
    public User register(@RequestBody User user){
        return service.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        System.out.println(user);
        return service.verify(user);
    }
    @GetMapping("/oauth2/redirect")
    public ResponseEntity<?> oauth2Redirect(@RequestParam String token) {
        return ResponseEntity.ok()
                .body(Map.of("token", token));
    }

}






