package ao.ngolahealth.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data 
public class AuthRequest {
    @Schema(description = "Email do Usuário", example = "anisio.munda@gmail.com")
    @NotBlank(message = "Email é Obrigatório")
    @Email(message = "O Email deve ser válido")
    private String email;

    @Schema(description = "Senha do Usuário", example = "anisio1234")
    @NotBlank(message = "Senha é Obrigatória")
    @Size(min = 6, message = "A Senha deve ter no mínimo 6 caracteres")
    private String password;
}
