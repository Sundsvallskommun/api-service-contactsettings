package se.sundsvall.contactsettings.api.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import se.sundsvall.contactsettings.api.validator.impl.ValidFindDelegatesParametersConstraintValidator;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFindDelegatesParametersConstraintValidator.class)
public @interface ValidFindDelegatesParameters {

	String message() default "not a valid findByParameters-request!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
