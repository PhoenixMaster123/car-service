package springboot.bg.harisauto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import springboot.bg.harisauto.validation.annotations.PasswordsMatch;

/**
 * PasswordsMatchValidator.java - Validator to check if two password fields match.
 *
 * @author Kristian Popov
 */
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {

  private String firstFieldName;
  private String secondFieldName;
  private String message;

  /** initialize the validator. */
  @Override
  public void initialize(PasswordsMatch constraintAnnotation) {
    this.firstFieldName = constraintAnnotation.first();
    this.secondFieldName = constraintAnnotation.second();
    this.message = constraintAnnotation.message();
  }

  /** validate the given value. */
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    Object firstFieldValue = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
    Object secondFieldValue = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

    boolean areEqual = (firstFieldValue == null && secondFieldValue == null)
                    || (firstFieldValue != null && firstFieldValue.equals(secondFieldValue));

    if (!areEqual) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(message)
          .addPropertyNode(secondFieldName)
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}