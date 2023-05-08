package se.sundsvall.contactsettings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.EMAIL;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContactSettingsResourceTest {

	private static final String PATH = "/settings";
	private static final String CONTACT_SETTING_ID = UUID.randomUUID().toString();
	private static final String PARTY_ID = UUID.randomUUID().toString();

	private static final String CONTACT_CHANNEL_DESTINATION = "0701234567";

	@Autowired
	private WebTestClient webTestClient;

	@LocalServerPort
	private int port;

	@Test
	void createContactSetting() {

		// Parameter values
		final var contactSetting = contactSettingCreateRequest();

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

		// TODO Add verifications
	}

	@Test
	void readContactSetting() {

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
		// TODO Add verifications
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
		// TODO Add verifications
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(CONTACT_SETTING_ID);
		assertThat(response.getContactChannels()).isEqualTo(contactSettingUpdateRequest.getContactChannels());
		assertThat(response.getAlias()).isEqualTo(contactSettingUpdateRequest.getAlias());
	}

	@Test
	void updateErrandEmptyRequest() {

		// Parameter values
		final var emptyInstance = ContactSettingUpdateRequest.create();
		final var updatedInstance = ContactSetting.create().withId(CONTACT_SETTING_ID);

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
	void deleteErrand() {
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
}
