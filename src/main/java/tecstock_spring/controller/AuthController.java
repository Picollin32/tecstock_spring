package tecstock_spring.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
            logger.error("Falha no login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    private String extractClientIp(String xForwardedFor, String remoteAddr) {
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return remoteAddr != null ? remoteAddr : "unknown";
    }
}
