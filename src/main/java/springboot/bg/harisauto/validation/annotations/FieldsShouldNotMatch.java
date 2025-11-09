package springboot.bg.harisauto.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import springboot.bg.harisauto.validation.FieldsShouldNotMatchValidator;

/**
 * FieldsShouldNotMatch.java - Custom annotation for validating that two fields do not match.
 *
 * @author Kristian Popov
 */
@Constraint(validatedBy = FieldsShouldNotMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsShouldNotMatch {

  /** Error message. */
  String message() default "Fields must not match";

  /** First field name. */
  String firstField();

  /** Second field name. */
  String secondField();

  /** Validation groups. */
  Class<?>[] groups() default {};

  /** Payload. */
  Class<? extends Payload>[] payload() default {};
}