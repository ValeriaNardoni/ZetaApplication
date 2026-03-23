package com.Zeta.demo_Zeta.service;
import com.Zeta.demo_Zeta.entity.User;
import com.Zeta.demo_Zeta.entity.UserRole;
import com.Zeta.demo_Zeta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;


    private final UserRepository userRepository;
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }    //ADD user

    public User register(User user) {
        log.info("Tentativo registrazione utente",
                kv("username", user.getUsername()),
                kv("email", user.getEmail()));

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("Username già esistente",
                    kv("username", user.getUsername()));
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email già in uso",
                    kv("email", user.getEmail()));
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user);

        log.info("Utente registrato con successo",
                kv("userId", saved.getId()),
                kv("username", saved.getUsername()),
                kv("email", saved.getEmail()));

        return saved;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> getAllUsers(){
        log.debug("Chiamato getAllUsers");
        return userRepository.findAll();
    }

    // Creazione admin (
    public User createInitialAdmin(User user) {
        user.setRole(UserRole.ADMIN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        log.info("Tentativo eliminazione utente",
                kv("userId", id));

        if (!userRepository.existsById(id)) {
            log.warn("Utente non trovato per eliminazione",
                    kv("userId", id));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        userRepository.deleteById(id);

        log.info("Utente eliminato con successo",
                kv("userId", id));
    }

    public User updateUser(Long id, User newUser) {

        log.info("Aggiornamento utente iniziato",
                kv("userId", id),
                kv("newUsername", newUser.getUsername()),
                kv("newEmail", newUser.getEmail()));

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Utente non trovato", kv("userId", id));
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        user.setUsername(newUser.getUsername());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());

        if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }

        User updated = userRepository.save(user);

        log.info("Utente aggiornato con successo",
                kv("userId", updated.getId()),
                kv("username", updated.getUsername()),
                kv("email", updated.getEmail()));

        return updated;
    }

    public User promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        user.setRole(UserRole.ADMIN);
        log.info("Aggiunto un nuovo Admin");
        return userRepository.save(user);
    }

    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    public List<User> getUsersByRole(UserRole role) {
        if (role == null) {
            return userRepository.findAll();
        }
        return userRepository.findByRole(role);
    }
}
