package co.edu.ucundinamarca.backendudecprac.domain.model.status;

/**
 * Seguimiento del contrato de práctica (independiente del estado de la práctica):
 * EN_CURSO (sin contrato aún) · CONTRATO_FIRMADO (PDF cargado) · FINALIZADA.
 * La empresa elige el valor libremente desde su panel; aquí solo se valida que sea uno de estos.
 */
public enum ContractTrackingStatus {
    EN_CURSO,
    CONTRATO_FIRMADO,
    FINALIZADA;

    public static ContractTrackingStatus from(String value) {
        return StatusParser.parse(ContractTrackingStatus.class, StatusParser.normalize(value), value, "el contrato");
    }
}
