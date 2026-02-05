package tecstock_spring.controller;

import jakarta.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private static final Logger logger = Logger.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            logger.info("Tentativa de login recebida");
            LoginResponseDTO response = authService.login(loginRequest);
            logger.info("Login bem-sucedido");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha no login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}
