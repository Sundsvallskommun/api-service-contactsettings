package se.sundsvall.contactsettings.api.validator.impl;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;

@ExtendWith(MockitoExtension.class)
class ValidContactSettingCreateRequestConstraintValidatorTest {

	@Mock
	private ConstraintViolationBuilder constraintViolationBuilderMock;

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@InjectMocks
	private ValidContactSettingCreateRequestConstraintValidator validator;

	@ParameterizedTest
	@MethodSource("validContactSettingCreateRequestProvider")
	void validContactChannel(final ContactSettingCreateRequest contactSettingCreateRequest, final boolean exprectedResult) {

		// Arrange
		lenient().when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var isValid = validator.isValid(contactSettingCreateRequest, constraintValidatorContextMock);

		// Assert
		assertThat(isValid).isEqualTo(exprectedResult);
	}

	private static Stream<Arguments> validContactSettingCreateRequestProvider() {
		return Stream.of(
			// Valid ContactSettingCreateRequest objects.
			Arguments.of(ContactSettingCreateRequest.create().withPartyId(randomUUID().toString()).withCreatedById(randomUUID().toString()), true),
			Arguments.of(ContactSettingCreateRequest.create().withPartyId(randomUUID().toString()), true),
			Arguments.of(ContactSettingCreateRequest.create().withCreatedById(randomUUID().toString()), true),

			// Invalid ContactSettingCreateRequest objects.
			Arguments.of(ContactSettingCreateRequest.create(), false),
			Arguments.of(null, false));
	}
}
