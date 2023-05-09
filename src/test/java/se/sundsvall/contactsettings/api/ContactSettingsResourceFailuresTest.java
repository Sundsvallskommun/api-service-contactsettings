package se.sundsvall.contactsettings.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.service.ContactSettingsService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.EMAIL;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.SMS;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContactSettingsResourceFailuresTest {

	private static final String PATH = "/settings";
	private static final String CONTACT_SETTING_ID = UUID.randomUUID().toString();

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ContactSettingsService contactSettingsServiceMock;

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
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.Void> se.sundsvall.contactsettings.api.ContactSettingsResource.createContactSetting(org.springframework.web.util.UriComponentsBuilder,se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest)");

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void createWithNotValidPartyId() {

		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId("not-valid-party-id");

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

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void createWithInvalidCreatedById() {

		final var body = ContactSettingCreateRequest.create()
			.withCreatedById("invalid-uuid")
			.withPartyId(randomUUID().toString());

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
			.containsExactlyInAnyOrder(tuple("createdById", "not a valid UUID"));

		// TODO Add verifications
	}

	@Test
	void createWithInvalidEmailChannel() {

		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId(UUID.randomUUID().toString())
			.withContactChannels(List.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("invalid")));

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
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The email destination value 'invalid' is not valid! Example of a valid value: hello@example.com"));

		// TODO Add verifications
	}

	@Test
	void createWithInvalidSMSChannel() {

		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId(UUID.randomUUID().toString())
			.withContactChannels(List.of(ContactChannel.create().withContactMethod(SMS).withDestination("invalid")));

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
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The SMS destination value 'invalid' is not valid! Example of a valid value: +46701234567"));

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
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: public org.springframework.http.ResponseEntity<se.sundsvall.contactsettings.api.model.ContactSetting> se.sundsvall.contactsettings.api.ContactSettingsResource.updateContactSetting(java.lang.String,se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest)");

		// TODO Add verifications
	}

	@Test
	void updateInvalidId() {

		final var body = ContactSettingUpdateRequest.create().withAlias("alias");
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

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithInvalidEmailChannel() {

		final var body = ContactSettingUpdateRequest.create()
			.withContactChannels(List.of(ContactChannel.create().withContactMethod(EMAIL).withDestination("invalid")));

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
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The email destination value 'invalid' is not valid! Example of a valid value: hello@example.com"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithInvalidSMSChannel() {

		final var body = ContactSettingUpdateRequest.create()
			.withContactChannels(List.of(ContactChannel.create().withContactMethod(SMS).withDestination("invalid")));

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
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The SMS destination value 'invalid' is not valid! Example of a valid value: +46701234567"));

		// TODO Add verifications
	}

	@Test
	void deleteInvalidId() {

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

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void readInvalidId() {

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

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void getContactSettingsInvalidPartyId() {

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

		verifyNoInteractions(contactSettingsServiceMock);
	}
}
