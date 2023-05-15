package se.sundsvall.contactsettings.api.validator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.EMAIL;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.SMS;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import se.sundsvall.contactsettings.api.model.ContactChannel;

@ExtendWith(MockitoExtension.class)
class ValidContactChannelConstraintValidatorTest {

	@Mock
	private ConstraintViolationBuilder constraintViolationBuilderMock;

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@InjectMocks
	private ValidContactChannelConstraintValidator validator;

	@ParameterizedTest
	@MethodSource("validContactChannelProvider")
	void validContactChannel(final ContactChannel contactChannel, final boolean exprectedResult) {

		// Arrange
		lenient().when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var isValid = validator.isValid(contactChannel, constraintValidatorContextMock);

		// Assert
		assertThat(isValid).isEqualTo(exprectedResult);
	}

	private static Stream<Arguments> validContactChannelProvider() {
		return Stream.of(
			// Valid email addresses.
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello@example.com"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello@example.co.uk"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello.2020@example.com"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello_2020@example.com"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("h@example.com"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("h@example-example.com"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("h@example-example-example.com"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("h@example.example-example.com"), true),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello.world-2020@example.com"), true),
			// Invalid email addresses.
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello"), false),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination(" "), false),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination(null), false),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello@2020@example.com"), false),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello@example_example.com"), false),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello.@example_example.com"), false),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello@example_example.com."), false),
			Arguments.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("hello@"), false),
			// Valid MSISDN:s.
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+46701234567"), true),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+46721234567"), true),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+46731234567"), true),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+46761234567"), true),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+46791234567"), true),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+123456789012345"), true),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+1234"), true),
			// Invalid MSISDN:s.
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("not-valid"), false),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination(" "), false),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination(null), false),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("46701234567"), false),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+06701234567"), false),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+1234567890123456"), false),
			Arguments.of(ContactChannel.create().withContactMethod(SMS).withDestination("+123"), false),
			// Invalid ContactChannel:s
			Arguments.of(ContactChannel.create(), false),
			Arguments.of(null, false));
	}
}
