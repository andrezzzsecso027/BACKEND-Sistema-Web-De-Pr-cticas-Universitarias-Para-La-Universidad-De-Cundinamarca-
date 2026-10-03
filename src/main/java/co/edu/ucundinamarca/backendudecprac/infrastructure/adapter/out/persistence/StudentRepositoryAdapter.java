package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentRepositoryAdapter implements StudentRepositoryPort {

    private final StudentJpaRepository jpaRepository;

    public StudentRepositoryAdapter(StudentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public Student saveStudent(Student student) {
        StudentEntity entity = new StudentEntity();
        entity.setDocumento(student.getDocument());
        entity.setIdUsuario(student.getIdUser());
        entity.setNombres(student.getName());
        entity.setApellidos(student.getLastName());
        entity.setDireccion(student.getAddres());
        entity.setTelefono(student.getPhoneNumber());
        entity.setSede(student.getHeadquarters());
        entity.setProgramaAcademico(student.getAcademicProgram());
        jpaRepository.save(entity);
        return student;
    }


    @Override
    public List<Student> findAllStudents() {
        List<StudentEntity> entidades = jpaRepository.findAll();
        List<Student> students = new ArrayList<>();

        for (StudentEntity entity : entidades) {
            Student est = new Student();
            est.setIdUser(entity.getIdUsuario());
            est.setDocument(entity.getDocumento());
            est.setName(entity.getNombres());
            est.setLastName(entity.getApellidos());
            est.setAddres(entity.getDireccion());
            est.setPhoneNumber(entity.getTelefono());
            est.setHeadquarters(entity.getSede());
            est.setAcademicProgram(entity.getProgramaAcademico());
            students.add(est);
        }
        return students;
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return jpaRepository.existsByDocumento(documento);
    }

    /*
    private Estudiante toDomain(EstudianteEntity entity) {
        return new Estudiante(entity.getId(), entity.getCodigo(), entity.getNombres(),
                entity.getApellidos(), entity.getCorreo(), entity.getCarrera(), entity.getSemestre());
    }

     */
}