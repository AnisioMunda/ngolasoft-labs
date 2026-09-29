package ao.ngolahealth.modules.auth.service;

import ao.ngolahealth.modules.auth.mapper.AuthMapper;

import java.util.Date;
import java.time.OffsetDateTime;
import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ao.ngolahealth.exceptions.EmailAlreadyExistsException;
import ao.ngolahealth.modules.auth.dto.AuthRequest;
import ao.ngolahealth.modules.auth.dto.AuthResponse;
import ao.ngolahealth.modules.auth.dto.RegisterRequest;
import ao.ngolahealth.modules.auth.entity.User;
import ao.ngolahealth.modules.auth.entity.enums.RegisterStatus;
import ao.ngolahealth.modules.auth.repository.UserRepository;
import ao.ngolahealth.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class AuthService {

    private final AuthMapper authMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlackListService tokenBlackListService;

    @Transactional 
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Este email já está cadastrado no sistema.");
        }

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .username(request.getUsername())
            .fullName(request.getFullname())
            .registerStatus(RegisterStatus.ACTIVE)
            .mustChangePassword(request.isMustChangePassword())
            .build();

        User savedUser = userRepository.save(user);

        String accesToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateToken(new HashMap<>(), savedUser);

        return authMapper.toAuthResponse(savedUser, accesToken, refreshToken);
    }

    // Login do Usuário
    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
            () -> new UsernameNotFoundException("Usuário não encontrado")
        );

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())  
            );
        }catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciais Inválidas - Verifique o email ou a password se estão correctas");
        }

        user.setLastLogin(OffsetDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateToken(new HashMap<>(), user);

        return authMapper.toAuthResponse(user, accessToken, refreshToken);
    }

    // Token Refresh
    public AuthResponse refreshtoken(String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token Inválido");
        }

        String refreshToken = authHeader.substring(7);
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
            .orElseThrow( () -> new UsernameNotFoundException("Usuário não encontrado."));

        if(!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Token Expirado Ou Inválido");
        }

        String newAccessToken = jwtService.generateToken(user);
        return authMapper.toAuthResponse(user, newAccessToken, refreshToken);
    }

    // Logout User
    public void logout(String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de Autorização Inválido ou em Falta.");
        }

        String token = authHeader.substring(7);

        try {
            Date expiraDate = jwtService.extractExpiration(token);
            if(expiraDate.after(new Date())) {
                tokenBlackListService.addToBlacklist(token, expiraDate);
            }
        } catch(Exception e) {
            throw new RuntimeException("Não pode deslogar, token inválido");
        }
    }
    
}
