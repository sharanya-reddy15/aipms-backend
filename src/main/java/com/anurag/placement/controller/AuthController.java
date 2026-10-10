package com.anurag.placement.controller;

import com.anurag.placement.entity.User;
import com.anurag.placement.service.JwtService;
import com.anurag.placement.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        User saved = userService.register(user);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Registration successful");
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole());
        return response;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        User loggedUser = userService.login(user.getEmail(), user.getPassword());
        String token = jwtService.generateToken(
                loggedUser.getEmail(),
                loggedUser.getRole());

        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("email", loggedUser.getEmail());
        response.put("role", loggedUser.getRole());
        return response;
    }
}
