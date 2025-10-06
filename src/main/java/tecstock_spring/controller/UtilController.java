package tecstock_spring.controller;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * Controller auxiliar para gerar senhas criptografadas com BCrypt.
 * Útil durante o desenvolvimento para criar hashes de senhas.
 * 
 * ATENÇÃO: Este controller deve ser REMOVIDO ou DESABILITADO em produção!
 */
@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UtilController {

    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(UtilController.class);

    /**
     * Gera um hash BCrypt para a senha fornecida.
     * 
     * Exemplo de uso: http://localhost:8081/api/util/gerar-senha?senha=admin123
     * 
     * @param senha A senha em texto plano
     * @return O hash BCrypt da senha
     */
    @GetMapping("/gerar-senha")
    public String gerarSenhaCriptografada(@RequestParam String senha) {
        logger.warn("⚠️ ATENÇÃO: Gerando senha criptografada para: " + senha);
        String senhaCriptografada = passwordEncoder.encode(senha);
        logger.info("Senha criptografada gerada com sucesso");
        return senhaCriptografada;
    }
}
