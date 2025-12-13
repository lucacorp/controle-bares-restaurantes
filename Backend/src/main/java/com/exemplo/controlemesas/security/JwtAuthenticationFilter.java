package com.exemplo.controlemesas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // ✅ usa JwtUtil existente
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔓 Endpoints públicos liberados (sem JWT)
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔒 Captura e valida o token JWT
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            // Do not block here; allow Spring Security to enforce access rules.
            log.debug("🔸 Sem token JWT para rota {} — continuando e deixando o Spring Security decidir.", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("⚠️ Token inválido para o usuário: {}", username);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido ou expirado");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Erro na validação do token JWT: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido ou expirado");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ✅ Endpoints públicos liberados
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth")
                || path.startsWith("/api/comandas/publicas")
                || path.startsWith("/api/comandas")
                || path.startsWith("/api/produtos")
                || path.startsWith("/api/itens-comanda")
                || path.startsWith("/api/cfop")
                || path.startsWith("/api/cst")
                || path.startsWith("/api/origem")
                || path.startsWith("/api/receitas")
                || path.startsWith("/api/configuracoes")
                || path.startsWith("/api/mesas")
                || path.startsWith("/comanda/publica")
                || path.startsWith("/public/comanda")
                || path.startsWith("/public")
                || path.equals("/")
                || path.equals("/favicon.ico");
    }
}