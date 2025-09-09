package com.egitron.gestaopedidos.api.auth;

import com.egitron.gestaopedidos.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder;

    @Value("${app.auth.username}")
    private String appUsername;

    @Value("${app.auth.password.bcrypt}")
    private String appPasswordHash;

    public AuthController(JwtService jwtService, BCryptPasswordEncoder encoder) {
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest body) {
        // normaliza inputs
        String u = body.getUsername() == null ? "" : body.getUsername().trim();
        String p = body.getPassword() == null ? "" : body.getPassword();

        if (!appUsername.equalsIgnoreCase(u) || !encoder.matches(p, appPasswordHash)) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
        String token = jwtService.generateToken(appUsername);
        return ResponseEntity.ok(new LoginResponse("Bearer", token));
    }

    // útil para testar rapidamente o token
    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(principal.getName());
    }

    // DTOs mínimos
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private final String tokenType;
        private final String accessToken;
        public LoginResponse(String tokenType, String accessToken) {
            this.tokenType = tokenType;
            this.accessToken = accessToken;
        }
        public String getTokenType() { return tokenType; }
        public String getAccessToken() { return accessToken; }
    }
}
