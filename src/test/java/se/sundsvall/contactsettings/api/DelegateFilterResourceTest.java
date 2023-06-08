package se.sundsvall.contactsettings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.service.DelegateFilterService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class DelegateFilterResourceTest {

	private final static String DELEGATE_ID = UUID.randomUUID().toString();
	private final static String DELEGATE_FILTER_ID = UUID.randomUUID().toString();

	@MockBean
	private DelegateFilterService delegateFilterServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void create() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create().withAttributeName("attribute").withAttributeValue("value").withOperator(EQUALS)));

		when(delegateFilterServiceMock.create(DELEGATE_ID, body)).thenReturn(body.withId(DELEGATE_FILTER_ID));

		// Act
		webTestClient.post()
			.uri(builder -> builder.path("/delegates/{id}/filters").build(Map.of(
				"id", DELEGATE_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().exists(LOCATION);

		// Assert
		verify(delegateFilterServiceMock).create(DELEGATE_ID, body);
	}

	@Test
	void read() {

		// Arrange
		when(delegateFilterServiceMock.read(DELEGATE_ID, DELEGATE_FILTER_ID)).thenReturn(Filter.create().withId(DELEGATE_FILTER_ID));

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/delegates/{id}/filters/{delegateFilterId}").build(Map.of(
				"id", DELEGATE_ID,
				"delegateFilterId", DELEGATE_FILTER_ID)))
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Filter.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(delegateFilterServiceMock).read(DELEGATE_ID, DELEGATE_FILTER_ID);
	}

	@Test
	void update() {

		// Arrange
		final var body = Filter.create()
			.withAlias("alias")
			.withRules(List.of(Rule.create().withAttributeName("attribute").withAttributeValue("value").withOperator(EQUALS)));

		when(delegateFilterServiceMock.update(DELEGATE_ID, DELEGATE_FILTER_ID, body)).thenReturn(body.withId(DELEGATE_FILTER_ID));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path("/delegates/{id}/filters/{delegateFilterId}").build(Map.of(
				"id", DELEGATE_ID,
				"delegateFilterId", DELEGATE_FILTER_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Filter.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(delegateFilterServiceMock).update(DELEGATE_ID, DELEGATE_FILTER_ID, body);
	}

	@Test
	void delete() {

		// Act
		webTestClient.delete()
			.uri(builder -> builder.path("/delegates/{id}/filters/{delegateFilterId}").build(Map.of(
				"id", DELEGATE_ID,
				"delegateFilterId", DELEGATE_FILTER_ID)))
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(delegateFilterServiceMock).delete(DELEGATE_ID, DELEGATE_FILTER_ID);
	}
}
