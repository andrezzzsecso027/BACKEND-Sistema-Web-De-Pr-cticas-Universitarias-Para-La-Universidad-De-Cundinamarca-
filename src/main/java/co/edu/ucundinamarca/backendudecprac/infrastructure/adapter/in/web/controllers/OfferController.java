package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateOfferUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetActiveOffersUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.OfferRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "http://localhost:4200")
public class OfferController {
    private final CreateOfferUseCase createOfferUseCase;
    private final GetActiveOffersUseCase getActiveOffersUseCase;
    public OfferController(CreateOfferUseCase createOfferUseCase, GetActiveOffersUseCase getActiveOffersUseCase) {
        this.createOfferUseCase = createOfferUseCase;
        this.getActiveOffersUseCase = getActiveOffersUseCase;
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

    @GetMapping("/estudiantes")
    public ResponseEntity<List<Offer>> listarOfertasParaEstudiantes(){
        try {
            List<Offer> ofertasActivas = getActiveOffersUseCase.getActiveOffers();
            if (ofertasActivas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(ofertasActivas);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
