package com.Zeta.demo_Zeta.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import com.Zeta.demo_Zeta.dto.UserDTO;
import com.Zeta.demo_Zeta.service.UserService;
import com.Zeta.demo_Zeta.entity.User;
import com.Zeta.demo_Zeta.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class ServiceController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

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

    @GetMapping("/users")
    public List<UserDTO> getAllUsersPublic() {
        return userService.getAllUsers().stream()
                .map(user -> new UserDTO(user.getUsername(), user.getEmail()))
                .toList();
    }
    @PutMapping("/me/password")
    public void changePassword(@RequestBody String newPassword,
                               Authentication authentication) {

        String username = authentication.getName();

        userService.changePassword(username, newPassword);
    }

}