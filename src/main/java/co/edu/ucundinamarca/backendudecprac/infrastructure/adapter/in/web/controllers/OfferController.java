package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateOfferUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.OfferRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "http://localhost:4200")
public class OfferController {
    private final CreateOfferUseCase createOfferUseCase;
    public OfferController(CreateOfferUseCase createOfferUseCase) {
        this.createOfferUseCase = createOfferUseCase;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearOferta(@RequestBody OfferRequest request) {
        try {
            System.out.println("NIT recibido en el DTO: [" + request.getNitCompanyAssociated() + "]");
            Offer offer = new Offer();
            offer.setPracticeName(request.getPracticeName());
            offer.setNitCompanyAssociated(request.getNitCompanyAssociated());
            offer.setPracticeDescription(request.getPracticeDescription());
            offer.setProfession(request.getProfession());
            offer.setSalary(request.getSalary());
            offer.setModality(request.getModality());
            offer.setLocation(request.getLocation());
            offer.setAvailableSpots(request.getAvailableSpots());
            offer.setClosingDate(request.getClosingDate());

            Offer ofertaCreada = createOfferUseCase.createOffer(offer);
            return ResponseEntity.status(HttpStatus.CREATED).body(ofertaCreada);
        }catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error interno en el servidor.");
        }
    }

}
