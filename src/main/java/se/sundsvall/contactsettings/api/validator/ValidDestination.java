package se.sundsvall.contactsettings.api.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hibernate.validator.constraints.CompositionType.OR;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.validator.constraints.ConstraintComposition;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Email;
import se.sundsvall.dept44.common.validators.annotation.ValidMSISDN;

/**
 * Composed constraint that defines a valid destination.
 *
 * The annotation is composed by:
 * <code>@ValidMSISDN</code> and <code>@Email</code>
 *
 * @see jakarta.validation.constraints.Email
 * @see se.sundsvall.dept44.common.validators.annotation.ValidMSISDN
 */
@ValidMSISDN
@Email
@ConstraintComposition(OR)
@ReportAsSingleViolation
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidDestination {

	String message() default "destination must be a valid MSIDN or a valid email address!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
