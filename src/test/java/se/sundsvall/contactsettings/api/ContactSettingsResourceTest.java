package se.sundsvall.contactsettings.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.EMAIL;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;

import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.service.ContactSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContactSettingsResourceTest {

	private static final String PATH = "/settings";
	private static final String CONTACT_SETTING_ID = randomUUID().toString();
	private static final String PARTY_ID = randomUUID().toString();
	private static final String CONTACT_CHANNEL_DESTINATION = "0701234567";

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ContactSettingsService contactSettingsServiceMock;

	@Test
	void create() {

		// Arrange
		final var contactSetting = contactSettingCreateRequest();

		when(contactSettingsServiceMock.createContactSetting(contactSetting)).thenReturn(CONTACT_SETTING_ID);

		// Act
		webTestClient.post()
			.uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(contactSetting)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location("/settings/" + CONTACT_SETTING_ID)
			.expectBody().isEmpty();

		// Assert
		verify(contactSettingsServiceMock).createContactSetting(contactSetting);
	}

	@Test
	void read() {

		// Arrange
		final var contactSetting = contactSetting();
		when(contactSettingsServiceMock.readContactSetting(CONTACT_SETTING_ID)).thenReturn(contactSetting);

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", CONTACT_SETTING_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull().isEqualTo(contactSetting);
		verify(contactSettingsServiceMock).readContactSetting(CONTACT_SETTING_ID);
	}

	@Test
	void readChildren() {

		// Arrange
		final var contectSettingChildren = List.of(contactSetting(), contactSetting());
		when(contactSettingsServiceMock.readContactSettingChildren(CONTACT_SETTING_ID)).thenReturn(contectSettingChildren);

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH + "/{id}/children").build(Map.of("id", CONTACT_SETTING_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(contactSettingsServiceMock).readContactSettingChildren(CONTACT_SETTING_ID);
	}

	@Test
	void updateFullRequest() {

		// Arrange
		final var contactSettingUpdateRequest = contactSettingUpdateRequest();
		final var contactSetting = contactSetting();

		when(contactSettingsServiceMock.updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class))).thenReturn(contactSetting);

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", CONTACT_SETTING_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(contactSettingUpdateRequest)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(CONTACT_SETTING_ID);
		assertThat(response.getContactChannels()).isEqualTo(contactSettingUpdateRequest.getContactChannels());
		assertThat(response.getAlias()).isEqualTo(contactSettingUpdateRequest.getAlias());
		verify(contactSettingsServiceMock).updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class));
	}

	@Test
	void updateEmptyRequest() {

		// Arrange
		final var emptyInstance = ContactSettingUpdateRequest.create();
		final var updatedInstance = ContactSetting.create().withId(CONTACT_SETTING_ID);

		when(contactSettingsServiceMock.updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class))).thenReturn(updatedInstance);

		// Act
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", CONTACT_SETTING_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(emptyInstance)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isEqualTo(updatedInstance);
		verify(contactSettingsServiceMock).updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class));
	}

	@Test
	void findByPartyIdAndQueryFilter() {

		// Arrange
		final var inputQuery = new LinkedMultiValueMap<String, String>();
		inputQuery.put("key1", List.of("value1", "value2"));
		inputQuery.put("key2", List.of("value3", "value4"));

		when(contactSettingsServiceMock.findByPartyIdAndQueryFilter(PARTY_ID, inputQuery)).thenReturn(List.of(
			ContactSetting.create(),
			ContactSetting.create()));

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH)
				.queryParam("partyId", PARTY_ID)
				.queryParams(inputQuery)
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull().hasSize(2);
		verify(contactSettingsServiceMock).findByPartyIdAndQueryFilter(PARTY_ID, inputQuery);
	}

	@Test
	void findByDestination() {

		// Arrange
		final var contactSettings = List.of(contactSetting());
		when(contactSettingsServiceMock.findByChannelsDestination(CONTACT_CHANNEL_DESTINATION)).thenReturn(contactSettings);

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH + "/contact-channels")
				.queryParam("destination", CONTACT_CHANNEL_DESTINATION)
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).hasSize(1);
		verify(contactSettingsServiceMock).findByChannelsDestination(CONTACT_CHANNEL_DESTINATION);
	}

	@Test
	void deleteContactSetting() {

		// Act
		webTestClient.delete()
			.uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", CONTACT_SETTING_ID)))
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		// Assert
		verify(contactSettingsServiceMock).deleteContactSetting(CONTACT_SETTING_ID);
	}

	private static ContactSettingCreateRequest contactSettingCreateRequest() {
		return ContactSettingCreateRequest.create()
			.withPartyId(PARTY_ID)
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(EMAIL)
				.withDestination("test.testsson@test.se")
				.withAlias("test")
				.withDisabled(true)))
			.withAlias("alias");
	}

	private static ContactSettingUpdateRequest contactSettingUpdateRequest() {
		return ContactSettingUpdateRequest.create()
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(EMAIL)
				.withDestination("test.testsson@test.se")
				.withAlias("test")
				.withDisabled(true)))
			.withAlias("alias");
	}

	private static ContactSetting contactSetting() {
		return ContactSetting.create()
			.withId(CONTACT_SETTING_ID)
			.withPartyId(PARTY_ID)
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(EMAIL)
				.withDestination("test.testsson@test.se")
				.withAlias("test")
				.withDisabled(true)))
			.withAlias("alias");
	}
}
