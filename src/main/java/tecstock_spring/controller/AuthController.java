package tecstock_spring.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tecstock_spring.dto.LoginRequestDTO;
import tecstock_spring.dto.LoginResponseDTO;
import tecstock_spring.service.AuthService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${security.trust-x-forwarded-for:false}")
    private boolean trustXForwardedFor;

    @Value("${security.trusted-proxy-ips:127.0.0.1,::1}")
    private String trustedProxyIps;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest,
                                   @RequestHeader(value = "X-Forwarded-For", required = false) String xff,
                                   jakarta.servlet.http.HttpServletRequest request) {
        try {
            logger.info("Tentativa de login recebida");
            String clientIp = extractClientIp(xff, request.getRemoteAddr());
            LoginResponseDTO response = authService.login(loginRequest, clientIp);
            logger.info("Login bem-sucedido");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha no login", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Falha na autenticação");
        }
    }

    private String extractClientIp(String xForwardedFor, String remoteAddr) {
        if (trustXForwardedFor && isTrustedProxy(remoteAddr) && xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return remoteAddr != null ? remoteAddr : "unknown";
    }

    private boolean isTrustedProxy(String remoteAddr) {
        if (remoteAddr == null || remoteAddr.isBlank()) {
            return false;
        }

        Set<String> trustedSet = Arrays.stream(trustedProxyIps.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        return trustedSet.contains(remoteAddr);
    }
}
