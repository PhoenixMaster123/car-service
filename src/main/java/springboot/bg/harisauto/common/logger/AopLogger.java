package springboot.bg.harisauto.common.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * RestConfig.java - Configuration class for RestTemplate setup.
 *
 * @author Kristian Popov
 */
@Aspect
@Component
public class AopLogger {

  /**
   * Writes an ENTER message for every specified method call to the logger.
   *
   * @param joinPoint JoinPoint
   */
  @Before("execution(* *(..)) && within(springboot.bg.harisauto.web.controller..*) "
      + "&& @annotation(springboot.bg.harisauto.common.logger.VeryImportant)")
  public void logBefore(JoinPoint joinPoint) {

    Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass().getName());

    if (logger.isDebugEnabled()) {
      logger.debug("ENTER: {}", joinPoint.getSignature().getName());
    }
  }

  /**
   * Writes an EXIT message for every specified method call to the logger.
   *
   * @param joinPoint JoinPoint
   */
  @AfterReturning("execution(* *(..)) && within(springboot.bg.harisauto.web.controller.*)")
  public void logAfter(JoinPoint joinPoint) {

    Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass().getName());

    if (logger.isDebugEnabled()) {
      logger.debug("EXIT: {}", joinPoint.getSignature().getName());
    }
  }

  /**
   * Writes information for a thrown Throwable to the logger.
   *
   * @param joinPoint JoinPoint
   * @param throwable Throwable
   */
  @AfterThrowing(pointcut = "execution(* *(..)) &&"
      + "within(springboot.bg.harisauto.web.controller.*)", throwing = "throwable")
  public void logAfterException(JoinPoint joinPoint, Throwable throwable) {

    Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass().getName());

    logger.warn("Exception occurred in method: {}.{} [{} - Detailed Message is: {}]",
            joinPoint.getTarget().getClass(),
            joinPoint.getSignature().getName(),
            throwable.getClass().getName(),
            throwable.getMessage());

    if (logger.isDebugEnabled()) {

      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      throwable.printStackTrace(printWriter);

      logger.debug(stringWriter.toString());
    }
  }

  /**
   * Returns any method result value after logging execution time.
   *
   * @see LogExecutionTime
   * @param joinPoint ProceedingJoinPoint
   * @return Object
   * @throws Throwable on anything that might be thrown during execution of the target method
   */
  @Around("@annotation(LogExecutionTime)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

    Logger logger = LogManager.getLogger("Performance");

    if (logger.isDebugEnabled()) {

      long start = System.currentTimeMillis();
      long executionTime = System.currentTimeMillis() - start;

      logger.debug("{} executed in {}ms", joinPoint.getSignature(), executionTime);
    }

    return joinPoint.proceed();
  }
}