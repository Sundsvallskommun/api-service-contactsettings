package se.sundsvall.contactsettings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.ContactSetting;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContactSettingsResourceFailuresTest {

	private static final String PATH = "/settings";
	private static final String CONTACT_SETTING_ID = UUID.randomUUID().toString();

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createMissingBody() {

		// Call
		final var response = webTestClient.post()
			.uri(PATH)
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.Void> "
			+ "se.sundsvall.contactsettings.api.ContactSettingsResource.createContactSetting(org.springframework.web.util.UriComponentsBuilder,se.sundsvall.contactsettings.api.model.ContactSetting)");

		// TODO Add verifications
	}

	@Test
	void createWithNotValidPartyId() {

		final var body = ContactSetting.create().withPartyId("not-valid-party-id");
		// Call
		final var response = webTestClient.post()
			.uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("partyId", "not a valid UUID"));

		// TODO Add verifications
	}

	@Test
	void updateMissingBody() {

		// Call
		final var response = webTestClient.patch()
			.uri(PATH + "/" + CONTACT_SETTING_ID)
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo("Required request body is missing: public org.springframework.http.ResponseEntity<se.sundsvall.contactsettings.api.model.ContactSetting> "
			+ "se.sundsvall.contactsettings.api.ContactSettingsResource.updateContactSetting(java.lang.String,se.sundsvall.contactsettings.api.model.ContactSetting)");

		// TODO Add verifications
	}

	@Test
	void updateNotValidPartyId() {

		final var body = ContactSetting.create().withPartyId("not-valid-party-id");
		// Call
		final var response = webTestClient.patch()
			.uri(PATH + "/" + CONTACT_SETTING_ID)
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("partyId", "not a valid UUID"));

		// TODO Add verifications
	}

	@Test
	void updateNotValidId() {

		final var body = ContactSetting.create().withPartyId(CONTACT_SETTING_ID).withAlias("alias");
		// Call
		final var response = webTestClient.patch()
			.uri(builder -> builder.path("/settings/{id}").build(Map.of("id", "not-valid-id")))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("updateContactSetting.id", "not a valid UUID"));

		// TODO Add verifications
	}

	@Test
	void deleteNotValidId() {

		// Call
		final var response = webTestClient.delete()
			.uri(builder -> builder.path("/settings/{id}").build(Map.of("id", "not-valid-id")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("deleteContactSetting.id", "not a valid UUID"));

		// TODO Add verifications
	}

	@Test
	void readNotValidId() {

		// Call
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/settings/{id}").build(Map.of("id", "not-valid-id")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("getContactSetting.id", "not a valid UUID"));

		// TODO Add verifications
	}

	@Test
	void getContactSettingsNotValidPartyId() {

		// Call
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).queryParam("partyId", "not-valid-party-id").build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("partyId", "not a valid UUID"));

		// TODO Add verifications
	}
}
