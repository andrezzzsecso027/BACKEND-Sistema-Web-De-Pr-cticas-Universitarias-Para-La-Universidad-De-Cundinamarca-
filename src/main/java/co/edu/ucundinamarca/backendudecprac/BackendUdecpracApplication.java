package co.edu.ucundinamarca.backendudecprac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BackendUdecpracApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendUdecpracApplication.class, args);
    }

}
