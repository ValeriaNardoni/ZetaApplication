package com.Zeta.demo_Zeta.bootstrap;

import com.Zeta.demo_Zeta.entity.User;
import com.Zeta.demo_Zeta.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserService userService;

    public AdminBootstrap(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.findByUsername("Valeria").isEmpty()) {

            User admin = new User();
            admin.setUsername("Valeria");
            admin.setFirstName("Valeria"); //
            admin.setLastName("Nardoni");
            admin.setEmail("valeria@example.com");
            admin.setPassword("admin123");          // Solo per demo: password hardcoded, in produzione usare variabile d'ambiente
            userService.createInitialAdmin(admin);

            System.out.println("Admin iniziale creato: Valeria");
        }
    }
}