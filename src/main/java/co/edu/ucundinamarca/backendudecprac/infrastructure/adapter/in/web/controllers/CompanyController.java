package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.model.LegalRepresentative;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.CompanyRecordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/api/registro/empresas")
    @CrossOrigin(origins = "http://localhost:4200")
    public class CompanyController {
        private final CreateCompanyUseCase createempresausecase;
        private final AdminUseCase adminusecase;
        private final CompanyRepositoryPort companyRepositoryPort;
        public CompanyController(CreateCompanyUseCase createempresausecase, AdminUseCase adminusecase, CompanyRepositoryPort companyRepositoryPort) {
            this.createempresausecase = createempresausecase;
            this.adminusecase = adminusecase;
            this.companyRepositoryPort = companyRepositoryPort;
        }

        @GetMapping
        public ResponseEntity<List<Company>> findAllEmpresas() {
            List<Company> lista = adminusecase.getAllCompanies();
            return ResponseEntity.ok(lista);
        }

        @PostMapping
        public ResponseEntity<Company> registrar(@RequestBody CompanyRecordRequest request) {
            User user = new User();
            user.setEmailAddres(request.getCorreo());
            user.setPassword(request.getContrasenia());
            user.setUserRol("empresa");
            LegalRepresentative representative = new LegalRepresentative();
            representative.setNameRepresentative(request.getNameRepresentive());
            representative.setLastNameRepresentative(request.getLastNameRepresentive());
            representative.setNumberDocument(request.getNumberDocument());
            Company company = new Company();
            company.setNit(request.getNit());
            company.setCompanyName(request.getNombreEmpresa());
            company.setCompanyType(request.getTipoEmpresa());
            company.setCompanyAddress(request.getDireccionEmpresa());
            company.setCompanyPhoneNumber(request.getTelefonoEmpresa());
            company.setLegalName(request.getLegalName());
            company.setCity(request.getCity());
            company.setDepartment(request.getDepartment());
            company.setLegalRepresentative(representative);


            Company creada = createempresausecase.createCompany(user, company);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        }
        @GetMapping("/nit/{nit}")
        public ResponseEntity<Company> obtenerEmpresaPorNit(@PathVariable String nit) {
            return companyRepositoryPort.findByNit(nit)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

    @PutMapping("/nit/{nit}/perfil")
    public ResponseEntity<Company> actualizarPerfilEmpresa(
            @PathVariable String nit,
            @RequestBody Company datosActualizados) {
        return companyRepositoryPort.findByNit(nit).map(existente -> {
            if (datosActualizados.getCompanyName() != null) existente.setCompanyName(datosActualizados.getCompanyName());
            if (datosActualizados.getLegalName() != null) existente.setLegalName(datosActualizados.getLegalName());
            if (datosActualizados.getCompanyType() != null) existente.setCompanyType(datosActualizados.getCompanyType());
            if (datosActualizados.getCompanyPhoneNumber() != null) existente.setCompanyPhoneNumber(datosActualizados.getCompanyPhoneNumber());
            if (datosActualizados.getDepartment() != null) existente.setDepartment(datosActualizados.getDepartment());
            if (datosActualizados.getCity() != null) existente.setCity(datosActualizados.getCity());
            if (datosActualizados.getCompanyAddress() != null) existente.setCompanyAddress(datosActualizados.getCompanyAddress());
            if (datosActualizados.getCompanyDescription() != null) existente.setCompanyDescription(datosActualizados.getCompanyDescription());
            if (datosActualizados.getLogoUrl() != null) existente.setLogoUrl(datosActualizados.getLogoUrl());
            if (datosActualizados.getRutPdfUrl() != null) existente.setRutPdfUrl(datosActualizados.getRutPdfUrl());

            if (datosActualizados.getLegalRepresentative() != null) {
                if (existente.getLegalRepresentative() == null) {
                    existente.setLegalRepresentative(new LegalRepresentative());
                }
                if (datosActualizados.getLegalRepresentative().getNameRepresentative() != null) {
                    existente.getLegalRepresentative().setNameRepresentative(
                            datosActualizados.getLegalRepresentative().getNameRepresentative()
                    );
                }
                if (datosActualizados.getLegalRepresentative().getLastNameRepresentative() != null) {
                    existente.getLegalRepresentative().setLastNameRepresentative(
                            datosActualizados.getLegalRepresentative().getLastNameRepresentative()
                    );
                }
            }

            Company guardada = companyRepositoryPort.saveCompany(existente);
            return ResponseEntity.ok(guardada);
        }).orElse(ResponseEntity.notFound().build());
    }
    }
