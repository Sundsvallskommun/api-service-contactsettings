package se.sundsvall.contactsettings.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.service.ContactSettingsService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.EMAIL;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContactSettingsResourceTest {

	private static final String PATH = "/settings";
	private static final String CONTACT_SETTING_ID = UUID.randomUUID().toString();
	private static final String PARTY_ID = UUID.randomUUID().toString();

	private static final String CONTACT_CHANNEL_DESTINATION = "0701234567";

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ContactSettingsService contactSettingsServiceMock;

	@LocalServerPort
	private int port;

	@Test
	void createContactSetting() {

		// Parameter values
		final var contactSetting = contactSettingCreateRequest();

		when(contactSettingsServiceMock.createContactSetting(contactSetting)).thenReturn(CONTACT_SETTING_ID);

		// Call
		webTestClient.post()
			.uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(contactSetting)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().exists(HttpHeaders.LOCATION)
			.expectBody().isEmpty();

		verify(contactSettingsServiceMock).createContactSetting(contactSetting);
		verifyNoMoreInteractions(contactSettingsServiceMock);
	}

	@Test
	void readContactSetting() {

		when(contactSettingsServiceMock.readContactSetting(CONTACT_SETTING_ID)).thenReturn(ContactSetting.create().withId(CONTACT_SETTING_ID));
		// Call
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", CONTACT_SETTING_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Verification
		verify(contactSettingsServiceMock).readContactSetting(CONTACT_SETTING_ID);
		verifyNoMoreInteractions(contactSettingsServiceMock);
		assertThat(response).isNotNull().isEqualTo(ContactSetting.create().withId(CONTACT_SETTING_ID));
	}

	@Test
	void readContactSettingByPartyId() {

		// Call
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH + "/party/{id}").build(Map.of("id", PARTY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Verification
		// TODO Add verifications
		assertThat(response).isNotNull().isEqualTo(ContactSetting.create().withPartyId(PARTY_ID));
	}

	@Test
	void updateContactSettingFullRequest() {
		// Parameter values
		final var contactSettingUpdateRequest = contactSettingUpdateRequest();
		final var contactSetting = contactSetting();

		when(contactSettingsServiceMock.updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class))).thenReturn(contactSetting);

		// Call
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

		// Verification
		verify(contactSettingsServiceMock).updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class));
		verifyNoMoreInteractions(contactSettingsServiceMock);
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(CONTACT_SETTING_ID);
		assertThat(response.getContactChannels()).isEqualTo(contactSettingUpdateRequest.getContactChannels());
		assertThat(response.getAlias()).isEqualTo(contactSettingUpdateRequest.getAlias());
	}

	@Test
	void updateContactSettingEmptyRequest() {

		// Parameter values
		final var emptyInstance = ContactSettingUpdateRequest.create();
		final var updatedInstance = ContactSetting.create().withId(CONTACT_SETTING_ID);

		when(contactSettingsServiceMock.updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class))).thenReturn(updatedInstance);

		// Call
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

		// Verification
		verify(contactSettingsServiceMock).updateContactSetting(eq(CONTACT_SETTING_ID), any(ContactSettingUpdateRequest.class));
		verifyNoMoreInteractions(contactSettingsServiceMock);
		assertThat(response).isEqualTo(updatedInstance);
	}

	@Test
	void getContactSettings() {
		// Parameter values
		final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		final MultiValueMap<String, String> filter = new LinkedMultiValueMap<>();
		parameters.add("partyId", PARTY_ID);
		filter.add("filterKey", List.of("filter1", "filter2").toString());
		parameters.addAll(filter);

		// Call
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).queryParams(parameters).build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Verification
		// TODO Add verifications
		assertThat(response).isNotNull().hasSize(1);
	}

	@Test
	void getContactSettingsByContactChannelDestination() {
		// Call
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH + "/contact-channel/{destination}").build(Map.of("destination", CONTACT_CHANNEL_DESTINATION)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(ContactSetting.class)
			.returnResult()
			.getResponseBody();

		// Verification
		// TODO Add verifications
		assertThat(response).isNotNull().hasSize(1);
	}

	@Test
	void deleteContactSetting() {
		webTestClient.delete()
			.uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", CONTACT_SETTING_ID)))
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		// TODO Add verifications
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
