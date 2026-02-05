package tecstock_spring.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tecstock_spring.util.JwtUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            
            if (jwtUtil.validateToken(jwt)) {
                final String username = jwtUtil.extractUsername(jwt);
                final Integer nivelAcesso = jwtUtil.extractNivelAcesso(jwt);
                final Long userId = jwtUtil.extractUserId(jwt);
                final Long consultorId = jwtUtil.extractConsultorId(jwt);
                final Long empresaId = jwtUtil.extractEmpresaId(jwt);

                String role;
                if (nivelAcesso == 0) {
                    role = "ROLE_SUPER_ADMIN";
                } else if (nivelAcesso == 1) {
                    role = "ROLE_ADMIN";
                } else {
                    role = "ROLE_CONSULTOR";
                }
                
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                SimpleGrantedAuthority nivelAuthority = new SimpleGrantedAuthority("NIVEL_ACESSO_" + nivelAcesso);
                
                java.util.List<SimpleGrantedAuthority> authorities = java.util.Arrays.asList(authority, nivelAuthority);
                
                logger.info("JWT Authentication - Username: " + username + ", NivelAcesso: " + nivelAcesso + ", Role: " + role);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                request.setAttribute("userId", userId);
                request.setAttribute("nivelAcesso", nivelAcesso);
                request.setAttribute("consultorId", consultorId);
                request.setAttribute("empresaId", empresaId);

                tecstock_spring.util.TenantContext.setCurrentNivelAcesso(nivelAcesso);
                if (empresaId != null) {
                    tecstock_spring.util.TenantContext.setCurrentEmpresaId(empresaId);
                }

                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                logger.debug("Token JWT validado para usuário: " + username + " (Nível: " + nivelAcesso + ")");
            }
        } catch (Exception e) {
            logger.error("Erro ao processar token JWT: " + e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            tecstock_spring.util.TenantContext.clear();
        }
    }
}
