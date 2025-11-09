package springboot.bg.harisauto.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import springboot.bg.harisauto.validation.PasswordsMatchValidator;

/**
 * PasswordsMatch.java - Custom annotation for validating that two password fields match.
 *
 * @author Kristian Popov
 */
@Constraint(validatedBy = PasswordsMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordsMatch {

  /** First field name. */
  String first();

  /** Second field name. */
  String second();

  /** Error message. */
  String message() default "The two fields do not match";

  /** Validation groups. */
  Class<?>[] groups() default {};

  /** Payload. */
  Class<? extends Payload>[] payload() default {};

  /** Allows multiple annotations on the same element. */
  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
    /** The nested @PasswordsMatch annotations. */
    PasswordsMatch[] value();
  }
}