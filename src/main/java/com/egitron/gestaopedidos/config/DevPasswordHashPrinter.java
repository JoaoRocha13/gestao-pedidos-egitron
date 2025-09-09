// src/main/java/com/egitron/gestaopedidos/config/DevPasswordHashPrinter.java
package com.egitron.gestaopedidos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DevPasswordHashPrinter {
    @Bean
    CommandLineRunner printHash(BCryptPasswordEncoder encoder) {
        return args -> {
            String raw = "admin123";
            System.out.println("=== DEV ONLY === BCrypt('" + raw + "') = " + encoder.encode(raw));
        };
    }
}
