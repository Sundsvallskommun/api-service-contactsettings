package se.sundsvall.contactsettings.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.Delegate;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.FindDelegatesParameters;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.service.DelegateService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class DelegateResourceTest {

	private static final String DELEGATE_ID = randomUUID().toString();

	@MockBean
	private DelegateService delegateServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void create() {

		// Arrange
		final var body = DelegateCreateRequest.create()
			.withAgentId(randomUUID().toString())
			.withPrincipalId(randomUUID().toString())
			.withFilters(List.of(Filter.create()
				.withAlias("filter")
				.withRules(List.of(Rule.create()
					.withAttributeName("attribute")
					.withOperator(EQUALS)
					.withAttributeValue("value")))));

		when(delegateServiceMock.create(any())).thenReturn(Delegate.create().withId(DELEGATE_ID));

		// Act
		webTestClient.post()
			.uri("/delegates")
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location("/delegates/" + DELEGATE_ID);

		// Assert
		verify(delegateServiceMock).create(body);
	}

	@Test
	void read() {

		// Arrange
		when(delegateServiceMock.read(DELEGATE_ID)).thenReturn(Delegate.create().withId(DELEGATE_ID));

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
		verify(delegateServiceMock).read(DELEGATE_ID);
	}

	@Test
	void delete() {

		// Act
		webTestClient.delete()
			.uri(builder -> builder.path("/delegates/{id}").build(Map.of("id", DELEGATE_ID)))
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(delegateServiceMock).delete(DELEGATE_ID);
	}

	@Test
	void findByAgentId() {

		// Arrange
		final var agentId = randomUUID().toString();
		when(delegateServiceMock.find(any())).thenReturn(List.of(Delegate.create()));

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates")
				.queryParam("agentId", agentId).build())
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(Delegate.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(delegateServiceMock).find(FindDelegatesParameters.create()
			.withAgentId(agentId)
			.withPrincipalId(null));
	}

	@Test
	void findByPrincipalId() {

		// Arrange
		final var principalId = randomUUID().toString();
		when(delegateServiceMock.find(any())).thenReturn(List.of(Delegate.create()));

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates")
				.queryParam("principalId", principalId).build())
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(Delegate.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(delegateServiceMock).find(FindDelegatesParameters.create()
			.withAgentId(null)
			.withPrincipalId(principalId));
	}

	@Test
	void findByPrincipalIdAndAgentId() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		when(delegateServiceMock.find(any())).thenReturn(List.of(Delegate.create()));

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates")
				.queryParam("agentId", agentId)
				.queryParam("principalId", principalId).build())
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(Delegate.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(delegateServiceMock).find(FindDelegatesParameters.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId));
	}
}
