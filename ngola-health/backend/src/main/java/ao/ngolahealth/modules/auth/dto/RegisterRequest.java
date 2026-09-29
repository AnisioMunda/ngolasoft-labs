package ao.ngolahealth.modules.auth.dto;

import java.time.OffsetDateTime;

import ao.ngolahealth.modules.auth.entity.Role;
import ao.ngolahealth.modules.auth.entity.enums.RegisterStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data 
public class RegisterRequest {
    
    @NotBlank(message = "Nome é Obrigatório")
    @Size(min = 15, max = 200, message = "Nome deve ter entre 15 e 200 caracteres")
    private String fullname;

    @NotBlank(message = "Nome do utilizador é obrigatório")
    @Size(min = 6, max = 50, message = "Nome deve ter entre 6 e 50 caracteres")
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Password é obrigatório")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message = "Senha deve conter letras maiúsculas, minúsculas e números"
    )
    private String password;

    private String phone;

    private String especiality;

    private String professionalCardNumber;

    @NotBlank(message = "Status do registo é obrigatório")
    private RegisterStatus registerStatus;

    @NotBlank(message = "O Campo de alteração de password é obrigatório")
    private boolean mustChangePassword;

    private OffsetDateTime lastLogin;

    private Role role;
}