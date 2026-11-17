package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;

import java.time.LocalDateTime;

/**
 * Modelo de lectura: una postulación del estudiante con los datos de la oferta y la empresa,
 * para la vista "Mis postulaciones".
 */
public record StudentPostulationSummary(
        Long idPostulation,
        Long idOffer,
        String offerName,
        String profession,
        String modality,
        String location,
        String companyName,
        PostulationStatus status,
        LocalDateTime datePostulation,
        LocalDateTime dateUpdate,
        String observations
) {
}
