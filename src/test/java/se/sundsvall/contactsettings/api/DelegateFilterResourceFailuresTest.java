package se.sundsvall.contactsettings.api;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;

import java.util.List;
import java.util.Map;

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
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.service.DelegateFilterService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class DelegateFilterResourceFailuresTest {

	private static final String PATH_TEMPLATE = "/{municipalityId}/delegates/{id}/filters";
	private static final String MUNICIPALITY_ID = "2281";
	private final static String DELEGATE_ID = randomUUID().toString();
	private final static String DELEGATE_FILTER_ID = randomUUID().toString();

	@MockBean
	private DelegateFilterService delegateFilterServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createWithMissingBody() {

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID)))
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.Void> se.sundsvall.contactsettings.api.DelegateFilterResource.create(java.lang.String,java.lang.String,se.sundsvall.contactsettings.api.model.Filter)");

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void createWithInvalidId() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create()
				.withAttributeName("key")
				.withAttributeValue("value")
				.withOperator(EQUALS)));

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of(
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
			.containsExactlyInAnyOrder(tuple("create.id", "not a valid UUID"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void createWithInvalidMunicipalityId() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create()
				.withAttributeName("key")
				.withAttributeValue("value")
				.withOperator(EQUALS)));

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of(
				"municipalityId", "invalid-id",
				"id", DELEGATE_ID)))
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

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void createWithEmptyRules() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(emptyList());

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID)))
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
			.containsExactlyInAnyOrder(tuple("rules", "must not be empty"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void createWithMissingRules() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(null);

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID)))
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
			.containsExactlyInAnyOrder(tuple("rules", "must not be empty"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void createWithInvalidRule() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create()));

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID)))
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
				tuple("rules[0].attributeName", "must not be blank"),
				tuple("rules[0].attributeValue", "must not be blank"),
				tuple("rules[0].operator", "must not be null"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void readWithInvalidId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", "invalid-id",
				"filterId", DELEGATE_FILTER_ID)))
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

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void readWithInvalidFilterId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"filterId", "invalid-id")))
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
			.containsExactlyInAnyOrder(tuple("read.filterId", "not a valid UUID"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void readWithInvalidMunicipalityId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", "invalid-id",
				"id", DELEGATE_ID,
				"filterId", DELEGATE_FILTER_ID)))
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

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void updateWithInvalidId() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create()
				.withAttributeName("key")
				.withAttributeValue("value")
				.withOperator(EQUALS)));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", "invalid-id",
				"filterId", DELEGATE_FILTER_ID)))
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

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void updateWithInvalidFilterId() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create()
				.withAttributeName("key")
				.withAttributeValue("value")
				.withOperator(EQUALS)));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"filterId", "invalid-id")))
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
			.containsExactlyInAnyOrder(tuple("update.filterId", "not a valid UUID"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void updateWithInvalidMunicipalityId() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create()
				.withAttributeName("key")
				.withAttributeValue("value")
				.withOperator(EQUALS)));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", "invalid-id",
				"id", DELEGATE_ID,
				"filterId", DELEGATE_FILTER_ID)))
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

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void updateWithMissingBody() {

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"filterId", DELEGATE_FILTER_ID)))
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: public org.springframework.http.ResponseEntity<se.sundsvall.contactsettings.api.model.Filter> se.sundsvall.contactsettings.api.DelegateFilterResource.update(java.lang.String,java.lang.String,java.lang.String,se.sundsvall.contactsettings.api.model.Filter)");

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void updateWithEmptyRules() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(emptyList());

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"filterId", DELEGATE_FILTER_ID)))
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
			.containsExactlyInAnyOrder(tuple("rules", "must not be empty"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void updateWithMissingRules() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(null);

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"filterId", DELEGATE_FILTER_ID)))
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
			.containsExactlyInAnyOrder(tuple("rules", "must not be empty"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void updateWithInvalidRule() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create()));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"filterId", DELEGATE_FILTER_ID)))
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
				tuple("rules[0].attributeName", "must not be blank"),
				tuple("rules[0].attributeValue", "must not be blank"),
				tuple("rules[0].operator", "must not be null"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void deleteWithInvalidId() {

		// Act
		final var response = webTestClient.delete()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", "invalid-id",
				"filterId", DELEGATE_FILTER_ID)))
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

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void deleteWithInvalidFilterId() {

		// Act
		final var response = webTestClient.delete()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"filterId", "invalid-id")))
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
			.containsExactlyInAnyOrder(tuple("delete.filterId", "not a valid UUID"));

		verifyNoInteractions(delegateFilterServiceMock);
	}

	@Test
	void deleteWithInvalidMunicipalityId() {

		// Act
		final var response = webTestClient.delete()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{filterId}").build(Map.of(
				"municipalityId", "invalid-id",
				"id", DELEGATE_ID,
				"filterId", DELEGATE_FILTER_ID)))
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
			.containsExactlyInAnyOrder(tuple("delete.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(delegateFilterServiceMock);
	}
}
