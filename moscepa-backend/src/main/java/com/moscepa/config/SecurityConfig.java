// Fichier : src/main/java/com/moscepa/config/SecurityConfig.java (Version Corrigée)

package com.moscepa.config;

import com.moscepa.security.AuthEntryPointJwt;
import com.moscepa.security.AuthTokenFilter;
import com.moscepa.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity // On garde @EnableMethodSecurity et on peut enlever @EnableWebSecurity qui est inclus
public class SecurityConfig {

    // ====================================================================
    // === CHANGEMENT 1 : On injecte les services, mais PAS le filtre.    ===
    // ====================================================================
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // ====================================================================
    // === CHANGEMENT 2 : On déclare le filtre comme un Bean.             ===
    // === Spring va le gérer correctement.                               ===
    // ====================================================================
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter( ) {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // On utilise le service injecté
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http ) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource( )))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Définir les routes PUBLIQUES
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/etudiants/inscrire").permitAll() // J'ai corrigé l'URL probable
                // Pour TOUT LE RESTE, exiger une authentification.
                .anyRequest().authenticated()
            );

        // On dit à Spring d'utiliser notre DaoAuthenticationProvider
        http.authenticationProvider(authenticationProvider( ));

        // ====================================================================
        // === CHANGEMENT 3 : On ajoute le filtre en appelant sa méthode Bean.===
        // ====================================================================
        http.addFilterBefore(authenticationJwtTokenFilter( ), UsernamePasswordAuthenticationFilter.class);

        return http.build( );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200" ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
