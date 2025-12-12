package com.exemplo.controlemesas.config;

import com.exemplo.controlemesas.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class WebSecurityConfig {

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // allow preflight CORS OPTIONS requests
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(
                    "/api/auth/**",
                    "/api/comanda-resumo/**",
                    "/comanda/publica/**",
                    "/public/**",
                    "/api/produtos/**",
                    "/api/itens-comanda/**",
                    "/api/comandas/**",   // 👈 libera acesso público para comandas
                    "/api/cfop/**",      // 👈 libera acesso público para lookup CFOP
                    "/api/cst/**",       // 👈 libera acesso público para lookup CST
                    "/api/origem/**",    // 👈 libera acesso público para lookup Origem
                    "/api/receitas/**",  // 👈 libera acesso público para receitas
                    "/api/configuracoes/**", // 👈 libera acesso público para configurações
                    "/api/mesas/**",      // 👈 libera acesso público para mesas
                    "/"
            ).permitAll()
            
            // 👇 Somente usuários com ROLE_COZINHEIRO (no banco: "COZINHEIRO") acessam a cozinha
            .requestMatchers("/cozinha/**").hasRole("COZINHEIRO")

            // 👇 O restante requer autenticação genérica
            .anyRequest().authenticated()
        )
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // register JWT filter after configuring session management
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Use allowed origin patterns to allow wildcard origins during debugging (works with credentials)
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        // Allow any header to avoid preflight rejection; keep Authorization exposed so frontend can read it
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}