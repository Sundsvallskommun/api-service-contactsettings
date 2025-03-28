package se.sundsvall.contactsettings.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.sundsvall.contactsettings.api.validator.impl.ValidContactSettingCreateRequestConstraintValidator;

@Documented
@Target({
	ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidContactSettingCreateRequestConstraintValidator.class)
public @interface ValidContactSettingCreateRequest {

	String message() default "not a valid ContactSettingCreateRequest";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
