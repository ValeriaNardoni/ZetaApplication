package com.Zeta.demo_Zeta.controller;

import com.Zeta.demo_Zeta.entity.User;
import com.Zeta.demo_Zeta.security.JwtService;
import com.Zeta.demo_Zeta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        var existingUser = userService.findByUsername(user.getUsername());

        if (existingUser.isEmpty() ||
                !passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return jwtService.generateToken(
                existingUser.get().getUsername(),
                existingUser.get().getRole().name()
        );
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }


}
