package se.sundsvall.contactsettings.api.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.contactsettings.api.model.FindDelegatesParameters;
import se.sundsvall.contactsettings.api.validator.ValidFindDelegatesParameters;

import static java.util.Objects.isNull;

public class ValidFindDelegatesParametersConstraintValidator implements ConstraintValidator<ValidFindDelegatesParameters, FindDelegatesParameters> {

	private static final String VALIDATION_MESSAGE = "At least one of agentId or principalId must be provided!";

	@Override
	public boolean isValid(final FindDelegatesParameters value, final ConstraintValidatorContext context) {
		if (isNull(value) || (isNull(value.getAgentId()) && isNull(value.getPrincipalId()))) {
			setValidationMessage(context, VALIDATION_MESSAGE);
			return false;
		}

		return true;
	}

	private void setValidationMessage(final ConstraintValidatorContext context, final String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}
