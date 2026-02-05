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
        logger.info("Tentativa de login recebida");

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
        rateLimitService.resetAttempts(username);

        String nomeCompleto;
        Integer nivelAcesso;
        Long consultorId = null;
        Long empresaId = null;
        LoginResponseDTO.ConsultorDTO consultorDTO = null;
        
        if (usuario.getConsultor() != null) {

            nomeCompleto = usuario.getConsultor().getNome();
            nivelAcesso = Integer.valueOf(usuario.getConsultor().getNivelAcesso());
            consultorId = usuario.getConsultor().getId();
            empresaId = usuario.getConsultor().getEmpresa() != null ? usuario.getConsultor().getEmpresa().getId() : null;

            consultorDTO = LoginResponseDTO.ConsultorDTO.builder()
                    .id(usuario.getConsultor().getId())
                    .nome(usuario.getConsultor().getNome())
                    .nivelAcesso(Integer.valueOf(usuario.getConsultor().getNivelAcesso()))
                    .build();
            
            logger.info("Usuário ID " + usuario.getId() + " vinculado ao consultor ID: " + usuario.getConsultor().getId());
        } else {

            nomeCompleto = usuario.getNomeCompleto() != null ? usuario.getNomeCompleto() : usuario.getNomeUsuario();
            nivelAcesso = usuario.getNivelAcesso();
            empresaId = usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null;
            logger.info("Usuário ID " + usuario.getId() + " admin sem consultor vinculado");
        }

        String token = jwtUtil.generateToken(usuario.getId(), usuario.getNomeUsuario(), nivelAcesso, consultorId, empresaId);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(usuario.getId());
        response.setNomeUsuario(usuario.getNomeUsuario());
        response.setNomeCompleto(nomeCompleto);
        response.setNivelAcesso(nivelAcesso);
        response.setToken(token);
        response.setConsultor(consultorDTO);

        if (empresaId != null) {
            if (usuario.getConsultor() != null && usuario.getConsultor().getEmpresa() != null) {
                LoginResponseDTO.EmpresaDTO empresaDTO = LoginResponseDTO.EmpresaDTO.builder()
                        .id(usuario.getConsultor().getEmpresa().getId())
                        .nomeFantasia(usuario.getConsultor().getEmpresa().getNomeFantasia())
                        .build();
                response.setEmpresa(empresaDTO);
            } else if (usuario.getEmpresa() != null) {
                LoginResponseDTO.EmpresaDTO empresaDTO = LoginResponseDTO.EmpresaDTO.builder()
                        .id(usuario.getEmpresa().getId())
                        .nomeFantasia(usuario.getEmpresa().getNomeFantasia())
                        .build();
                response.setEmpresa(empresaDTO);
            }
        }
                
        logger.info("Login realizado com sucesso - ID: " + usuario.getId() + " (Nível: " + nivelAcesso + ")");
        return response;
    }
}
