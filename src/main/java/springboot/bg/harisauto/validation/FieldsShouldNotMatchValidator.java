package springboot.bg.harisauto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import springboot.bg.harisauto.validation.annotations.FieldsShouldNotMatch;

/**
 * FieldsShouldNotMatchValidator.java - Validator to ensure that two specified fields do not match.
 *
 * @author Kristian Popov
 */
public class FieldsShouldNotMatchValidator implements
             ConstraintValidator<FieldsShouldNotMatch, Object> {

  private String firstFieldName;
  private String secondFieldName;

  /** initialize the validator. */
  @Override
  public void initialize(FieldsShouldNotMatch constraintAnnotation) {
    this.firstFieldName = constraintAnnotation.firstField();
    this.secondFieldName = constraintAnnotation.secondField();
  }

  /** validate the given value. */
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    Object firstFieldValue = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
    Object secondFieldValue = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

    return firstFieldValue == null || !firstFieldValue.equals(secondFieldValue);
  }
}