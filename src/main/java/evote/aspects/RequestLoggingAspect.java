package evote.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * RequestLoggingAspect - Aspect für Request-Logging
 *
 * Verantwortung: Loggt alle HTTP-Requests an den VoteController und VoterController
 * in die requests.log Datei. Dies hilft beim Debugging und bei der Überwachung
 * der API-Nutzung.
 */
@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger requestLogger = LoggerFactory.getLogger("evote.requests");
    private static final String REQUEST_START_TIME = "REQUEST_START_TIME";

    /**
     * Loggt vor dem Aufruf einer Controller-Methode
     */
    @Before("execution(* evote.*.infrastructure.web.*Controller.*(..))")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = getHttpServletRequest();
            if (request != null) {
                String method = request.getMethod();
                String uri = request.getRequestURI();
                String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
                String methodName = joinPoint.getSignature().getName();
                String args = Arrays.toString(joinPoint.getArgs());

                requestLogger.info("[REQUEST START] {} {} - Method: {} - Args: {}",
                        method, uri + queryString, methodName, args);

                // Speichere die Start-Zeit für die AfterReturning
                request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
            }
        } catch (Exception e) {
            requestLogger.error("Fehler beim Logging des Request-Starts: {}", e.getMessage());
        }
    }

    /**
     * Loggt nach erfolgreichem Abschluss einer Controller-Methode
     */
    @After("execution(* evote.*.infrastructure.web.*Controller.*(..))")
    public void logAfterControllerMethod(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = getHttpServletRequest();
            if (request != null) {
                String method = request.getMethod();
                String uri = request.getRequestURI();
                String methodName = joinPoint.getSignature().getName();

                // Berechne die Dauer des Requests
                Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
                long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

                requestLogger.info("[REQUEST END] {} {} - Method: {} - Duration: {}ms",
                        method, uri, methodName, duration);
            }
        } catch (Exception e) {
            requestLogger.error("Fehler beim Logging des Request-Endes: {}", e.getMessage());
        }
    }

    /**
     * Hilfsmethode zum Abrufen des aktuellen HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            // Keine HTTP-Anfrage in diesem Kontext
        }
        return null;
    }

}

