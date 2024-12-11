package se.sundsvall.contactsettings.api;

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

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.service.ContactSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContactSettingsResourceFailuresTest {

	private static final String PATH_TEMPLATE = "/{municipalityId}/settings";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String CONTACT_SETTING_ID = randomUUID().toString();

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private ContactSettingsService contactSettingsServiceMock;

	@Test
	void createWithMissingBody() {

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.Void> se.sundsvall.contactsettings.api.ContactSettingsResource.create(java.lang.String,se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest)");

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void createWithInvalidMunicipalityId() {

		// Arrange
		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId(randomUUID().toString());

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", "invalid-municipality-id")))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("create.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void createWithNotValidPartyId() {

		// Arrange
		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId("not-valid-party-id");

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
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

		// Arrange
		final var body = ContactSettingCreateRequest.create()
			.withCreatedById("invalid-uuid")
			.withPartyId(randomUUID().toString());

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("createdById", "not a valid UUID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void createWithInvalidEmailDestination() {

		// Arrange
		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId(randomUUID().toString())
			.withContactChannels(List.of(ContactChannel.create()
				.withAlias("Alias")
				.withContactMethod(EMAIL)
				.withDestination("invalid")));

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The email destination value 'invalid' is not valid! Example of a valid value: hello@example.com"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void createWithInvalidSMSDestination() {

		// Arrange
		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId(randomUUID().toString())
			.withContactChannels(List.of(ContactChannel.create()
				.withAlias("Alias")
				.withContactMethod(SMS)
				.withDestination("invalid")));

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The SMS destination value 'invalid' is not valid! Example of a valid value: +46701234567"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void createWithInvalidContactChannel() {

		// Arrange
		final var body = ContactSettingCreateRequest.create()
			.withCreatedById(randomUUID().toString())
			.withPartyId(randomUUID().toString())
			.withContactChannels(List.of(ContactChannel.create()));

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("contactChannels[0]", "not a valid ContactChannel"),
				tuple("contactChannels[0].alias", "must not be blank"),
				tuple("contactChannels[0].contactMethod", "must not be null"),
				tuple("contactChannels[0].destination", "must not be blank"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithMissingBody() {

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", CONTACT_SETTING_ID)))
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: public org.springframework.http.ResponseEntity<se.sundsvall.contactsettings.api.model.ContactSetting> se.sundsvall.contactsettings.api.ContactSettingsResource.update(java.lang.String,java.lang.String,se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest)");

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithInvalidId() {

		// Arrange
		final var body = ContactSettingUpdateRequest.create().withAlias("alias");

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", "invalid-id")))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("update.id", "not a valid UUID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithInvalidMunicipalityId() {

		// Arrange
		final var body = ContactSettingUpdateRequest.create().withAlias("alias");

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", "invalid-id",
				"id", CONTACT_SETTING_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("update.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithInvalidEmailDestination() {

		// Arrange
		final var body = ContactSettingUpdateRequest.create()
			.withContactChannels(List.of(ContactChannel.create()
				.withAlias("Alias")
				.withContactMethod(EMAIL)
				.withDestination("invalid")));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", CONTACT_SETTING_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The email destination value 'invalid' is not valid! Example of a valid value: hello@example.com"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithInvalidSMSDestination() {

		// Arrange
		final var body = ContactSettingUpdateRequest.create()
			.withContactChannels(List.of(ContactChannel.create()
				.withAlias("Alias")
				.withContactMethod(SMS)
				.withDestination("invalid")));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", CONTACT_SETTING_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("contactChannels[0]", "The SMS destination value 'invalid' is not valid! Example of a valid value: +46701234567"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void updateWithInvalidContactChannel() {

		// Arrange
		final var body = ContactSettingUpdateRequest.create()
			.withContactChannels(List.of(ContactChannel.create()));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", CONTACT_SETTING_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("contactChannels[0]", "not a valid ContactChannel"),
				tuple("contactChannels[0].alias", "must not be blank"),
				tuple("contactChannels[0].contactMethod", "must not be null"),
				tuple("contactChannels[0].destination", "must not be blank"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void deleteWithInvalidId() {

		// Act
		final var response = webTestClient.delete()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", "invalid-id")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("delete.id", "not a valid UUID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void readWithInvalidId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", "invalid-id")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("read.id", "not a valid UUID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void readWithInvalidMunicipalityId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{id}").build(Map.of(
				"municipalityId", "invalid-id",
				"id", CONTACT_SETTING_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("read.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void findByPartyIdAndQueryFilterWithInvalidPartyId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE)
				.queryParam("partyId", "invalid-partyId")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("findByPartyIdAndQueryFilter.partyId", "not a valid UUID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void findByPartyIdAndQueryFilterWithMissingPartyId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo("Required request parameter 'partyId' for method parameter type String is not present");

		verifyNoInteractions(contactSettingsServiceMock);
	}

	@Test
	void findByPartyIdAndQueryFilterWithInvalidMunicipalityId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE)
				.queryParam("partyId", randomUUID().toString())
				.build(Map.of("municipalityId", "invalid-id")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("findByPartyIdAndQueryFilter.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(contactSettingsServiceMock);
	}
}
