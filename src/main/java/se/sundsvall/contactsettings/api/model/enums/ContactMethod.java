package se.sundsvall.contactsettings.api.model.enums;

import static org.zalando.problem.Status.BAD_REQUEST;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import org.zalando.problem.Problem;

@Schema(description = "ContactMethod model", enumAsRef = true)
public enum ContactMethod {

	SMS,
	EMAIL;

	private static final String INVALID_VALUE_ERROR = "Invalid value for enum ContactMethod: %s";

	public static ContactMethod toEnum(String value) {
		return Stream.of(ContactMethod.values())
			.filter(e -> e.name().equalsIgnoreCase(value))
			.findAny()
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, INVALID_VALUE_ERROR.formatted(value)));
	}
}
