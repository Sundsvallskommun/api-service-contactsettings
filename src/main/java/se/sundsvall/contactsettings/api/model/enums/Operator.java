package se.sundsvall.contactsettings.api.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Schema(description = "Operator model", enumAsRef = true)
public enum Operator {

	EQUALS,
	NOT_EQUALS;

	private static final String INVALID_VALUE_ERROR = "Invalid value for enum Operator: %s";

	public static Operator toEnum(String value) {
		return Stream.of(Operator.values())
			.filter(e -> e.name().equalsIgnoreCase(value))
			.findAny()
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, INVALID_VALUE_ERROR.formatted(value)));
	}
}
