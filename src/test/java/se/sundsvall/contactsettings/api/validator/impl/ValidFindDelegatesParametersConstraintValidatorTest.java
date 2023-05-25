package se.sundsvall.contactsettings.api.validator.impl;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

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
import se.sundsvall.contactsettings.api.model.FindDelegatesParameters;

@ExtendWith(MockitoExtension.class)
class ValidFindDelegatesParametersConstraintValidatorTest {

	@Mock
	private ConstraintViolationBuilder constraintViolationBuilderMock;

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@InjectMocks
	private ValidFindDelegatesParametersConstraintValidator validator;

	@ParameterizedTest
	@MethodSource("validContactChannelProvider")
	void validContactChannel(final FindDelegatesParameters getDelegatesParameters, final boolean exprectedResult) {

		// Arrange
		lenient().when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var isValid = validator.isValid(getDelegatesParameters, constraintValidatorContextMock);

		// Assert
		assertThat(isValid).isEqualTo(exprectedResult);
	}

	private static Stream<Arguments> validContactChannelProvider() {
		return Stream.of(
			// Valid email addresses.
			Arguments.of(FindDelegatesParameters.create().withAgentId(randomUUID().toString()), true),
			Arguments.of(FindDelegatesParameters.create().withPrincipalId(randomUUID().toString()), true),
			Arguments.of(FindDelegatesParameters.create().withAgentId(randomUUID().toString()).withPrincipalId(randomUUID().toString()), true),

			// Invalid email addresses.
			Arguments.of(FindDelegatesParameters.create(), false),
			Arguments.of(null, false));
	}
}
