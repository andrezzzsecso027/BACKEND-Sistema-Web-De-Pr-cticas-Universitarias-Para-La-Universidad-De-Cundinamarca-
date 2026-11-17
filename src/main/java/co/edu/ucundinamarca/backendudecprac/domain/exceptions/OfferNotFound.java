package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

public class OfferNotFound extends ResourceNotFoundException {
    public OfferNotFound(String message) {
        super(message);
    }
}
