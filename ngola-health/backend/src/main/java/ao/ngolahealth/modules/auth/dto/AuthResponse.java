package ao.ngolahealth.modules.auth.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data 
@Schema(description = "Resposta de Usuário Autenticado com Sucesso com token JWT")
public class AuthResponse {

    @Schema(description = "ID do Usuário", example = "00000000-0000-0000-0000-000000000001")
    private UUID id;

    @Schema(description = "Nome Completo do Usuário", example = "Mauro Fonseca de Andrade")
    private String fullname;

    @Schema(description = "Nome de Utilizador (usuário)", example = "mauro")
    private String username;

    @Schema(description = "Email do Usuário", example = "mauro.fonseca@gmail.com")
    private String email;

    @Schema(description = "Token de Acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Refresh Token JWT para renovação de token", example = "hhyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
}
