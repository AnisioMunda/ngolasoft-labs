package ao.ngolahealth.modules.auth.mapper;

import org.springframework.stereotype.Component;

import ao.ngolahealth.modules.auth.dto.AuthResponse;
import ao.ngolahealth.modules.auth.entity.User;

@Component 
public class AuthMapper {
    public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(user.getId());
        authResponse.setFullname(user.getFullName());
        authResponse.setUsername(user.getUsername());
        authResponse.setEmail(user.getEmail());
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        return authResponse;    
    }
}
