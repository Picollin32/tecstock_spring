package tecstock_spring.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Chave secreta - será movida para variável de ambiente
    @Value("${jwt.secret:TecStockSecretKeyForJWTTokenGenerationAndValidation2024!@#$%SecureKey123456789}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas em milissegundos
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Gera token JWT
    public String generateToken(Long userId, String nomeUsuario, Integer nivelAcesso, Long consultorId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("nomeUsuario", nomeUsuario);
        claims.put("nivelAcesso", nivelAcesso);
        if (consultorId != null) {
            claims.put("consultorId", consultorId);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(nomeUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Extrai o nome de usuário do token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extrai o userId do token
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    // Extrai o nível de acesso do token
    public Integer extractNivelAcesso(String token) {
        return extractClaims(token).get("nivelAcesso", Integer.class);
    }

    // Extrai o consultorId do token (pode ser null)
    public Long extractConsultorId(String token) {
        return extractClaims(token).get("consultorId", Long.class);
    }

    // Valida o token
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica se o token expirou
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extrai a data de expiração
    private Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    // Extrai todas as claims do token
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Valida o token sem verificar username
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
