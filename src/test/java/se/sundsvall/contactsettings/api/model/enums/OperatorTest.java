package se.sundsvall.contactsettings.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.zalando.problem.ThrowableProblem;

class OperatorTest {

	@Test
	void testValidEnumValues() {
		Stream.of(Operator.values()).forEach(e -> assertThat(e).isEqualTo(Operator.toEnum(e.name())));
	}

	@Test
	void testUnknownEnumValue() {
		final var exception = assertThrows(ThrowableProblem.class, () -> Operator.toEnum("UNKNOWN"));

		assertThat(exception.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(exception.getMessage()).isEqualTo("Bad Request: Invalid value for enum Operator: UNKNOWN");
	}
}
