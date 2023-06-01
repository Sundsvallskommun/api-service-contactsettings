package se.sundsvall.contactsettings.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.service.DelegateService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class DelegateResourceFailuresTest {

	@MockBean
	private DelegateService delegateServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createWithMissingBody() {

		// Act
		final var response = webTestClient.post()
			.uri("/delegates")
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
			"Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.Void> se.sundsvall.contactsettings.api.DelegateResource.create(org.springframework.web.util.UriComponentsBuilder,se.sundsvall.contactsettings.api.model.DelegateCreateRequest)");

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void createWithInvalidAgentId() {

		// Arrange
		final var body = DelegateCreateRequest.create()
			.withAgentId("invalid-agentId")
			.withPrincipalId(randomUUID().toString());

		// Act
		final var response = webTestClient.post()
			.uri("/delegates")
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
			.containsExactlyInAnyOrder(tuple("agentId", "not a valid UUID"));

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void createWithInvalidPrincipalId() {

		// Arrange
		final var body = DelegateCreateRequest.create()
			.withAgentId(randomUUID().toString())
			.withPrincipalId("invalid-principalId");

		// Act
		final var response = webTestClient.post()
			.uri("/delegates")
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
			.containsExactlyInAnyOrder(tuple("principalId", "not a valid UUID"));

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void createWithInvalidFilterRule() {

		// Arrange
		final var body = DelegateCreateRequest.create()
			.withAgentId(randomUUID().toString())
			.withPrincipalId(randomUUID().toString())
			.withFilters(List.of(Filter.create()
				.withId(UUID.randomUUID().toString())
				.withRules(List.of(Rule.create())))); // Missing all Rule-attributes

		// Act
		final var response = webTestClient.post()
			.uri("/delegates")
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
				tuple("filters[0].rules[0].attributeName", "must not be blank"),
				tuple("filters[0].rules[0].attributeValue", "must not be blank"),
				tuple("filters[0].rules[0].operator", "must not be null"));

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void deleteWithInvalidId() {

		// Act
		final var response = webTestClient.delete()
			.uri(builder -> builder.path("/delegates/{id}").build(Map.of("id", "not-valid-id")))
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

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void readWithInvalidId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates/{id}").build(Map.of("id", "not-valid-id")))
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

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void findWithMissingParameters() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates").build())
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
			.containsExactlyInAnyOrder(tuple("findDelegatesParameters", "One of agentId or principalId must be provided!"));

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void findWithInvalidAgentId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates")
				.queryParam("agentId", "not-valid-uuid")
				.queryParam("principalId", randomUUID().toString()).build())
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
			.containsExactlyInAnyOrder(tuple("agentId", "not a valid UUID"));

		verifyNoInteractions(delegateServiceMock);
	}

	@Test
	void findWithInvalidPrincipalId() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates")
				.queryParam("agentId", randomUUID().toString())
				.queryParam("principalId", "not-valid-uuid").build())
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
			.containsExactlyInAnyOrder(tuple("principalId", "not a valid UUID"));

		verifyNoInteractions(delegateServiceMock);
	}
}
