package com.egitron.gestaopedidos.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Filtro JWT minimalista:
 * - Extrai "Authorization: Bearer <token>"
 * - Valida o token via JwtService
 * - Se válido, coloca Authentication no SecurityContext (stateless)
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtService.isValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String subject = jwtService.extractSubject(token);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    subject,
                                    null,
                                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {
                // Token inválido/expirado → segue sem auth; SecurityConfig bloqueará /api/**
            }
        }

        filterChain.doFilter(request, response);
    }
}
