package com.egitron.gestaopedidos.config;

import com.egitron.gestaopedidos.security.JwtAuthFilter;
import com.egitron.gestaopedidos.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtService jwtService) {
        return new JwtAuthFilter(jwtService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests(auth -> auth
                .antMatchers("/auth/login", "/actuator/health", "/mock-api/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults()); // não usamos basic, mas deixa default
        return http.build();
    }
}
