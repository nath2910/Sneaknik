package backend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class JwtSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public JwtSecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return (HttpServletRequest req) -> {
            CorsConfiguration cfg = new CorsConfiguration();
            cfg.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://sneaknik-2.onrender.com"
            ));
            cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

            // ⭐ important : en dev, on autorise tous les headers (sinon ça casse vite)
           cfg.setAllowedHeaders(List.of("*"));


            // optionnel mais pratique si un jour tu renvoies un token dans un header
           cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
            cfg.setExposedHeaders(List.of("Authorization"));
            cfg.setAllowCredentials(false);
            return cfg;
        };
    }
//     @Bean
// public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
//     return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
// }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ⭐ important : si pas authentifié => 401 (pas 403)
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/auth/change-password").authenticated()
                .anyRequest().authenticated()
            )

            // on enlève les mécanismes par défaut
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable());

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
