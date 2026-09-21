package ao.ngolahealth.modules.hospitals.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HospitalType {
    PUBLIC("Público"),
    PRIVATE("Privado"),
    SPECIALIZED("Especializado");

    private final String descricao;
}
