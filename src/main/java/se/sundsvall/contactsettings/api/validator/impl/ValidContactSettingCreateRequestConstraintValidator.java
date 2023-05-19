package se.sundsvall.contactsettings.api.validator.impl;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.allNull;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.validator.ValidContactSettingCreateRequest;

public class ValidContactSettingCreateRequestConstraintValidator implements ConstraintValidator<ValidContactSettingCreateRequest, ContactSettingCreateRequest> {

	private static final String VALIDATION_MESSAGE = "One of partyId or createdById must be provided!";

	@Override
	public boolean isValid(ContactSettingCreateRequest value, ConstraintValidatorContext context) {
		if (isNull(value)) {
			return false;
		}

		if (allNull(value.getPartyId(), value.getCreatedById())) {
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
