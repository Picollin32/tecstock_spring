package tecstock_spring.controller;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UtilController {

    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(UtilController.class);

    @GetMapping("/gerar-senha")
    public String gerarSenhaCriptografada(@RequestParam String senha) {
        logger.warn("⚠️ ATENÇÃO: Gerando senha criptografada para: " + senha);
        String senhaCriptografada = passwordEncoder.encode(senha);
        logger.info("Senha criptografada gerada com sucesso");
        return senhaCriptografada;
    }
}
