package tecstock_spring.service;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tecstock_spring.dto.LoginRequestDTO;
import tecstock_spring.dto.LoginResponseDTO;
import tecstock_spring.model.Usuario;
import tecstock_spring.repository.UsuarioRepository;
import tecstock_spring.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RateLimitService rateLimitService;
    private static final Logger logger = Logger.getLogger(AuthServiceImpl.class);

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        String username = loginRequest.getNomeUsuario();
        logger.info("Tentativa de login para usuário");
        
        // Verifica se o usuário está bloqueado por excesso de tentativas
        if (rateLimitService.isBlocked(username)) {
            long minutesRemaining = rateLimitService.getBlockedMinutesRemaining(username);
            logger.warn("Tentativa de login bloqueada - Bloqueado por mais " + minutesRemaining + " minutos");
            throw new RuntimeException("Muitas tentativas de login. Tente novamente em " + minutesRemaining + " minutos.");
        }
        
        Usuario usuario = usuarioRepository.findByNomeUsuario(username);
        
        if (usuario == null) {
            rateLimitService.recordFailedAttempt(username);
            int remainingAttempts = rateLimitService.getRemainingAttempts(username);
            logger.warn("Tentativa de login com credenciais inválidas - Tentativas restantes: " + remainingAttempts);
            throw new RuntimeException("Credenciais inválidas");
        }

        if (!passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {
            rateLimitService.recordFailedAttempt(username);
            int remainingAttempts = rateLimitService.getRemainingAttempts(username);
            logger.warn("Tentativa de login com credenciais inválidas - Tentativas restantes: " + remainingAttempts);
            throw new RuntimeException("Credenciais inválidas");
        }

        // Login bem-sucedido - reseta tentativas
        rateLimitService.resetAttempts(username);

        // Usar dados do próprio usuário (admin) ou do consultor vinculado
        String nomeCompleto;
        Integer nivelAcesso;
        Long consultorId = null;
        LoginResponseDTO.ConsultorDTO consultorDTO = null;
        
        if (usuario.getConsultor() != null) {
            // Usuário vinculado a um consultor
            nomeCompleto = usuario.getConsultor().getNome();
            nivelAcesso = Integer.valueOf(usuario.getConsultor().getNivelAcesso());
            consultorId = usuario.getConsultor().getId();
            
            // Criar DTO do consultor
            consultorDTO = LoginResponseDTO.ConsultorDTO.builder()
                    .id(usuario.getConsultor().getId())
                    .nome(usuario.getConsultor().getNome())
                    .nivelAcesso(Integer.valueOf(usuario.getConsultor().getNivelAcesso()))
                    .build();
            
            logger.info("Usuário vinculado ao consultor ID: " + usuario.getConsultor().getId());
        } else {
            // Usuário independente (admin)
            nomeCompleto = usuario.getNomeCompleto() != null ? usuario.getNomeCompleto() : usuario.getNomeUsuario();
            nivelAcesso = usuario.getNivelAcesso();
            logger.info("Usuário admin sem consultor vinculado");
        }

        // Gerar token JWT real
        String token = jwtUtil.generateToken(usuario.getId(), usuario.getNomeUsuario(), nivelAcesso, consultorId);

        // Criar objeto diretamente ao invés de usar builder
        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(usuario.getId());
        response.setNomeUsuario(usuario.getNomeUsuario());
        response.setNomeCompleto(nomeCompleto);
        response.setNivelAcesso(nivelAcesso);
        response.setToken(token);
        response.setConsultor(consultorDTO); // Adicionar dados do consultor
                
        logger.info("Login realizado com sucesso - ID: " + usuario.getId() + " (Nível: " + nivelAcesso + ")");
        return response;
    }
}
