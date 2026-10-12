package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.PostulationDetailResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetPostulationsUseCaseImpl implements GetPostulationsUseCase {
    private final PostulationRepositoryPort postulationRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    private final StudentProfileRepositoryPort profileRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public GetPostulationsUseCaseImpl(PostulationRepositoryPort postulationRepositoryPort, StudentRepositoryPort studentRepositoryPort, StudentProfileRepositoryPort profileRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.postulationRepositoryPort = postulationRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.profileRepositoryPort = profileRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<PostulationDetailResponse> getPostulationsByOfferId(Long idOffer) {
        List<Postulation> postulaciones = postulationRepositoryPort.getPostulationsByOfferId(idOffer);

        return postulaciones.stream().map(postulacion -> {
            PostulationDetailResponse response = new PostulationDetailResponse();

            response.setIdPostulation(postulacion.getIdPostulation());
            response.setDatePostulation(postulacion.getDatePostulation());
            response.setStatusPostulation(postulacion.getStatusPostulation());
            response.setDocumentStudent(postulacion.getDocumentNumberStudent());

            Student estudiante = studentRepositoryPort.findByDocumento(postulacion.getDocumentNumberStudent());

            if (estudiante != null) {
                response.setStudentFullName(estudiante.getName() + " " + estudiante.getLastName());


                User usuario = userRepositoryPort.findById(estudiante.getIdUser()).orElse(null);
                if (usuario != null) {
                    response.setStudentEmail(usuario.getEmailAddres());
                }


                StudentProfile perfil = profileRepositoryPort.findByDocumentStudent(postulacion.getDocumentNumberStudent());

                if (perfil != null) {
                    response.setSkills(perfil.getSkills());
                    response.setEnglishLevel(perfil.getEnglishLevel());
                    response.setLifesheet(perfil.getLinkLifesheet());
                    response.setGithubUrl(perfil.getGithubUrl());
                }
            }
            return response;
        }).collect(Collectors.toList());
    }
}
