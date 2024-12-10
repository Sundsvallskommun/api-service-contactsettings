package se.sundsvall.contactsettings.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.sundsvall.contactsettings.api.validator.impl.ValidContactChannelConstraintValidator;

/**
 * Validates a contact channel.
 *
 * If contactMethod "EMAIL" is used, the destination must be a valid email address according to
 * https://www.rfc-editor.org/rfc/rfc5322.txt.
 *
 * If contactMethod "SMS" is used, the destination must be a valid MSISDN according to
 * https://en.wikipedia.org/wiki/MSISDN. The MSISDN should also be preceded by a '+'-character.
 *
 */
@Documented
@Target({
	ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidContactChannelConstraintValidator.class)
public @interface ValidContactChannel {

	String message() default "not a valid ContactChannel";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
