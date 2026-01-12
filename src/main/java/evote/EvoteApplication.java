package evote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Einstiegspunkt der Anwendung und Start des Spring-Boot-Kontexts.
 */
@SpringBootApplication
public class EvoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(EvoteApplication.class, args);
    }
}
