package tecstock_spring.service;

import tecstock_spring.dto.LoginRequestDTO;
import tecstock_spring.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequest, String clientIp);
}
