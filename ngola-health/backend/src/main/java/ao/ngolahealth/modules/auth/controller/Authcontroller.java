package ao.ngolahealth.modules.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ao.ngolahealth.modules.auth.dto.AuthRequest;
import ao.ngolahealth.modules.auth.dto.AuthResponse;
import ao.ngolahealth.modules.auth.dto.RefreshTokenRequest;
import ao.ngolahealth.modules.auth.dto.RegisterRequest;
import ao.ngolahealth.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Serviço de Autenticação e Cadastro de Usuários")
public class Authcontroller {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Registrar um novo utilizador",
        description = "Criar um novo Usuário no Sistema"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Usuário Criado com Sucesso",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Parâmetros Inválidos"
            ),
            @ApiResponse(
                responseCode = "409",
                description = "O Email já está em uso"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erro Interno do Servidor"
            )
        }
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        URI uri = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/users/{id}")
            .buildAndExpand(response.getId())
            .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Autenticar Usuário",
        description = "Fazer a autenticação do usuário com email e senha"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Autenticação Bem Sucedida",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Credenciais Inválidas"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Entrada de dados Inválida"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erro Interno do servidor"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Acesso Negado"
            )
        }
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh Token JWT",
        description = "Gerar Um novo token utilizando um token válido"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Token Atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Token Inválido ou expirado"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erro Interno do servidor"
            )
        }
    )
    public ResponseEntity<AuthResponse> refresh (@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshtoken("Bearer "+request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Deslogar Usuário",
        description = "Fechar Sessão do usuário",
        security = @SecurityRequirement(name = "JavaBearerAuth")
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuário deslogado com Sucesso"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Faltando Argumento ou mal formação do cabeçalho"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Token Inválido ou expirado"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erro Interno do Servidor"
            )
        }
    )
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }
}
