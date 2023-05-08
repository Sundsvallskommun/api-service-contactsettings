package se.sundsvall.contactsettings.api.validator.impl;

import static java.util.Objects.isNull;

import java.util.Optional;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.validator.ValidContactChannel;

public class ValidContactChannelConstraintValidator implements ConstraintValidator<ValidContactChannel, ContactChannel> {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
	private static final Pattern MSISDN_PATTERN = Pattern.compile("^\\+[1-9][\\d]{3,14}$");
	private static final String EMAIL_VALIDATION_MESSAGE = "The email destination value '%s' is not valid! Example of a valid value: hello@example.com";
	private static final String SMS_VALIDATION_MESSAGE = "The SMS destination value '%s' is not valid! Example of a valid value: +46701234567";

	@Override
	public boolean isValid(final ContactChannel value, final ConstraintValidatorContext context) {

		if (isNull(value) || isNull(value.getContactMethod())) {
			return false;
		}

		return switch (value.getContactMethod()) {
			case EMAIL -> {
				if (!EMAIL_PATTERN.matcher(Optional.ofNullable(value.getDestination()).orElse("")).matches()) {
					setValidationMessage(context, String.format(EMAIL_VALIDATION_MESSAGE, value.getDestination(), EMAIL_PATTERN.pattern()));
					yield false;
				}
				yield true;
			}
			case SMS -> {
				if (!MSISDN_PATTERN.matcher(Optional.ofNullable(value.getDestination()).orElse("")).matches()) {
					setValidationMessage(context, String.format(SMS_VALIDATION_MESSAGE, value.getDestination(), MSISDN_PATTERN.pattern()));
					yield false;
				}
				yield true;
			}
		};
	}

	private void setValidationMessage(final ConstraintValidatorContext context, final String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}
