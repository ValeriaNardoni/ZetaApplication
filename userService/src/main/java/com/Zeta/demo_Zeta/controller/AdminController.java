package com.Zeta.demo_Zeta.controller;

import com.Zeta.demo_Zeta.dto.PromoteUserDTO;
import com.Zeta.demo_Zeta.entity.UserRole;
import com.Zeta.demo_Zeta.service.UserService;
import com.Zeta.demo_Zeta.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers(); // Admin vede tutto
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public User createAdmin(@RequestBody User user) {
        return userService.createInitialAdmin(user);
    }

    @PutMapping("/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> promoteUserToAdmin(@RequestBody PromoteUserDTO dto) {
        User promoted = userService.promoteToAdmin(dto.getId());
        return ResponseEntity.ok("Utente " + promoted.getUsername() + " promosso a ADMIN!");
    }
    @GetMapping("/users/byRole")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsersByRole(@RequestParam(required = false) UserRole role) {
        return userService.getUsersByRole(role);
    }
}