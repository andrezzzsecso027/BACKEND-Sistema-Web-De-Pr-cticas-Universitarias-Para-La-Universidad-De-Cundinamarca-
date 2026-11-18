package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateOfferUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetActiveOffersUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.OfferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
public class OfferController {
    private final CreateOfferUseCase createOfferUseCase;
    private final GetActiveOffersUseCase getActiveOffersUseCase;

    public OfferController(CreateOfferUseCase createOfferUseCase, GetActiveOffersUseCase getActiveOffersUseCase) {
        this.createOfferUseCase = createOfferUseCase;
        this.getActiveOffersUseCase = getActiveOffersUseCase;
    }

    @PostMapping("/crear")
    public ResponseEntity<Offer> crearOferta(@RequestBody OfferRequest request) {
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
        return ResponseEntity.status(HttpStatus.CREATED).body(createOfferUseCase.createOffer(offer));
    }

    @GetMapping("/estudiantes")
    public ResponseEntity<List<Offer>> listarOfertasParaEstudiantes() {
        List<Offer> ofertasActivas = getActiveOffersUseCase.getActiveOffers();
        return ofertasActivas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ofertasActivas);
    }
}
