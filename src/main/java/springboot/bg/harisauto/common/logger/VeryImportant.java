package springboot.bg.harisauto.common.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * VeryImportant.java - Custom annotation to mark very important classes or methods.
 *
 * @see AopLogger
 * @author Kristian Popov
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VeryImportant {}