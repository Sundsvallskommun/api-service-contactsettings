package se.sundsvall.contactsettings.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.service.DelegateFilterService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class DelegateFilterResourceTest {

	private static final String PATH_TEMPLATE = "/{municipalityId}/delegates/{id}/filters";
	private static final String LOCATION_TEMPLATE = "/{municipalityId}/delegates/{id}/filters/{delegateFilterId}";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String DELEGATE_ID = randomUUID().toString();
	private static final String DELEGATE_FILTER_ID = randomUUID().toString();

	@MockitoBean
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
			.uri(builder -> builder.path(PATH_TEMPLATE).build(Map.of("municipalityId", MUNICIPALITY_ID, "id", DELEGATE_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location(fromPath(LOCATION_TEMPLATE).buildAndExpand(MUNICIPALITY_ID, DELEGATE_ID, DELEGATE_FILTER_ID).toString());

		// Assert
		verify(delegateFilterServiceMock).create(DELEGATE_ID, body);
	}

	@Test
	void read() {

		// Arrange
		when(delegateFilterServiceMock.read(DELEGATE_ID, DELEGATE_FILTER_ID)).thenReturn(Filter.create().withId(DELEGATE_FILTER_ID));

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{delegateFilterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
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
			.withRules(List.of(Rule.create()
				.withAttributeName("attribute")
				.withAttributeValue("value")
				.withOperator(EQUALS)));

		when(delegateFilterServiceMock.update(DELEGATE_ID, DELEGATE_FILTER_ID, body)).thenReturn(body.withId(DELEGATE_FILTER_ID));

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{delegateFilterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
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
			.uri(builder -> builder.path(PATH_TEMPLATE + "/{delegateFilterId}").build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"id", DELEGATE_ID,
				"delegateFilterId", DELEGATE_FILTER_ID)))
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(delegateFilterServiceMock).delete(DELEGATE_ID, DELEGATE_FILTER_ID);
	}
}
