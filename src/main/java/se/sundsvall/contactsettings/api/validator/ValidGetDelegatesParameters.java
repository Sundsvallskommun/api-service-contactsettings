package se.sundsvall.contactsettings.api.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import se.sundsvall.contactsettings.api.validator.impl.ValidGetDelegatesParametersConstraintValidator;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidGetDelegatesParametersConstraintValidator.class)
public @interface ValidGetDelegatesParameters {

	String message() default "not a valid ContactChannel";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}