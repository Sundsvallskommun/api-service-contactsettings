package se.sundsvall.contactsettings.api.validator.impl;

import static java.util.Objects.isNull;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.contactsettings.api.model.GetDelegatesParameters;
import se.sundsvall.contactsettings.api.validator.ValidGetDelegatesParameters;

public class ValidGetDelegatesParametersConstraintValidator implements ConstraintValidator<ValidGetDelegatesParameters, GetDelegatesParameters> {

	private static final String VALIDATION_MESSAGE = "One of agentId or principalId must be provided!";

	private void setValidationMessage(final ConstraintValidatorContext context, final String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}

	@Override
	public boolean isValid(final GetDelegatesParameters value, final ConstraintValidatorContext context) {
		if (isNull(value) || (isNull(value.getAgentId()) && isNull(value.getPrincipalId()))) {
			setValidationMessage(context, VALIDATION_MESSAGE);
			return false;
		}

		return true;
	}
}
