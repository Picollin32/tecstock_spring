package tecstock_spring.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tecstock_spring.filter.JwtAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF (para APIs REST com JWT)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuração CORS centralizada
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless (JWT)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sem autenticação)
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Endpoints administrativos (apenas ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasRole("ADMIN")
                        
                        // Ajustes de estoque e atualização de peças requerem ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/pecas/atualizar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/pecas/ajustar-estoque").hasRole("ADMIN")
                        
                        // Desbloquear OS requer ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/ordens-servico/desbloquear/**").hasRole("ADMIN")
                        
                        // Tipo de pagamento requer ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/tipos-pagamento/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tipos-pagamento/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tipos-pagamento/**").hasRole("ADMIN")
                        
                        // Todos os outros endpoints requerem autenticação (ADMIN ou CONSULTOR)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir localhost em qualquer porta para desenvolvimento
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",        // Qualquer porta no localhost
                "http://127.0.0.1:*"         // Localhost alternativo
                // Em produção, substitua por URLs específicas
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
