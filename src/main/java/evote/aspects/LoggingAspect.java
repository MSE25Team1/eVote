package evote.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * LoggingAspect - Aspect für Exception-Logging
 *
 * Verantwortung: Loggt alle Exceptions aus den Service-Methoden in die error.log
 * Dies ist ein AOP-Aspekt, der automatisch für alle Service-Methoden ausgelöst wird.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger("evote.errors");

    /**
     * Loggt vor dem Aufruf einer Service-Methode
     */
    @Before("execution(* evote.*.application.*Service.*(..))")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        logger.info("Aufruf von Service-Methode: {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    /**
     * Loggt alle Exceptions, die in Service-Methoden auftreten
     */
    @AfterThrowing(
            pointcut = "execution(* evote.*.application.*Service.*(..))",
            throwing = "error"
    )
    public void logServiceErrors(JoinPoint joinPoint, Throwable error) {
        logger.error("Fehler in {}.{}: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                error.getMessage(),
                error);
    }

}

