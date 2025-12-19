package evote.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Globale CORS-Konfiguration für das eVote-System.
 *
 * Infrastruktur-Konfiguration:
 * - Kein Teil der Domänenlogik
 * - Gilt für alle REST-Adapter (/api/**)
 * - Erlaubt Frontend-Zugriff von localhost:3000
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
