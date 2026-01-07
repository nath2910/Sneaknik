package backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF (Cross-Site Request Forgery) car on utilise généralement des tokens JWT pour les API REST
            .csrf(csrf -> csrf.disable())
            // Activer la configuration CORS définie plus bas
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Définir les règles d'accès aux URL
            .authorizeHttpRequests(auth -> auth
                // Autoriser tout le monde à accéder aux routes d'authentification ET aux ventes
                .requestMatchers("/auth/**", "/snkVente/**").permitAll()
                // Toutes les autres requêtes nécessitent d'être connecté
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // L'URL de votre Frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Autoriser tous les headers (Authorization, Content-Type...)
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}