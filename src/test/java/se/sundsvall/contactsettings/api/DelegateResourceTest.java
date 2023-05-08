package se.sundsvall.contactsettings.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.Delegate;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.DelegateUpdateRequest;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class DelegateResourceTest {

	private static final String DELEGATE_ID = UUID.randomUUID().toString();

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void create() {

		// Arrange
		final var body = DelegateCreateRequest.create()
			.withAgentId(randomUUID().toString())
			.withPrincipalId(randomUUID().toString())
			.withFilter(Map.of("filterKey", List.of("value")));

		// Act
		webTestClient.post()
			.uri("/delegates")
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().exists(LOCATION);

		// Assert
		// TODO Add verifications
	}

	@Test
	void read() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates/{id}").build(Map.of("id", DELEGATE_ID)))
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Delegate.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		// TODO Add verifications
	}

	@Test
	void update() {

		// Arrange
		final var body = DelegateUpdateRequest.create()
			.withFilter(Map.of("filterKey", List.of("value")));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path("/delegates/{id}").build(Map.of("id", DELEGATE_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Delegate.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();

		// TODO Add verifications
	}

	@Test
	void delete() {

		// Act
		webTestClient.delete()
			.uri(builder -> builder.path("/delegates/{id}").build(Map.of("id", DELEGATE_ID)))
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		// TODO Add verifications
	}

	@Test
	void find() {

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates")
				.queryParam("agentId", randomUUID().toString()).build())
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(Delegate.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		// TODO Add verifications
	}
}
