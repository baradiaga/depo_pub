package com.moscepa.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // <--
// AJOUT
// DE
// L'IMPORT
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.moscepa.security.AuthEntryPointJwt;
import com.moscepa.security.AuthTokenFilter;
import com.moscepa.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // <--- ANNOTATION AJOUTÉE ICI
public class SecurityConfig {

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthEntryPointJwt unauthorizedHandler,
            DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf
                .disable()).exceptionHandling(exception -> exception.authenticationEntryPoint(
                        unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // ENDPOINTS PUBLICS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**",
                                "/swagger-ui.html")
                        .permitAll().requestMatchers("/api/users/create-test-user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/etudiants").permitAll()

                        // ENDPOINTS AUTHENTIFIÉS
                        .requestMatchers(HttpMethod.GET, "/api/admin/users/role/ENSEIGNANT")
                        .authenticated().requestMatchers(HttpMethod.GET, "/api/chapitres")
                        .authenticated().requestMatchers(HttpMethod.GET, "/api/chapitres/**")
                        .authenticated().requestMatchers(HttpMethod.POST, "/api/matieres")
                        .authenticated().requestMatchers(HttpMethod.GET, "/api/matieres/**")
                        .authenticated().requestMatchers("/api/tests/**").authenticated()
                        .requestMatchers("/api/progression/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/parcours/etudiant/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/permissions/me").authenticated()

                        // CONFIGURATION SPÉCIFIQUE AUX RÔLES
                        .requestMatchers(HttpMethod.POST, "/api/chapitres")
                        .hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.POST, "/api/questionnaires")
                        .hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.POST,
                                "/api/questionnaires/generer-depuis-banque")
                        .hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.GET,
                                "/api/unites-enseignement/*/elements-constitutifs")
                        .hasAnyRole("ADMIN", "RESPONSABLE_FORMATION", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.POST,
                                "/api/unites-enseignement/*/elements-constitutifs")
                        .hasAnyRole("ADMIN", "RESPONSABLE_FORMATION")
                        .requestMatchers(HttpMethod.PUT, "/api/elements-constitutifs/**")
                        .hasAnyRole("ADMIN", "RESPONSABLE_FORMATION")
                        .requestMatchers(HttpMethod.DELETE, "/api/elements-constitutifs/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/unites-enseignement")
                        .hasAnyRole("ADMIN", "RESPONSABLE_FORMATION")
                        .requestMatchers(HttpMethod.PUT, "/api/unites-enseignement/**")
                        .hasAnyRole("ADMIN", "RESPONSABLE_FORMATION")
                        .requestMatchers(HttpMethod.DELETE, "/api/unites-enseignement/**")
                        .hasRole("ADMIN").requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // RÈGLE FINALE : TOUTE AUTRE REQUÊTE NÉCESSITE UNE AUTHENTIFICATION
                        // La sécurité spécifique pour /api/fonctionnalites/** sera gérée par
                        // @PreAuthorize
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
