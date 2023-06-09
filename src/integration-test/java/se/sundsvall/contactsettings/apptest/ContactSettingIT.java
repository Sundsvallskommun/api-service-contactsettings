package se.sundsvall.contactsettings.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.contactsettings.Application;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/ContactSettingIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class ContactSettingIT extends AbstractAppTest {

	private static final String PATH = "/settings";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final String CONTACT_SETTING_ID = "41e31470-150b-4db1-b3c1-c8f4108051ab";
	private static final String PARTY_ID = "7903f7a9-325a-4a49-929a-d5952fef5c9a";

	@Autowired
	private DelegateRepository delegateRepository;

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	@Test
	void test01_createContactSetting() {
		final var location = setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.sendRequestAndVerifyResponse()
			.getResponseHeaders().get("Location").get(0);

		setupCall()
			.withServicePath(location.substring(location.indexOf(PATH)))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_readContactSetting() {
		setupCall()
			.withServicePath(PATH + "/" + CONTACT_SETTING_ID)
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_updateContactSetting() {
		setupCall()
			.withServicePath(PATH + "/" + CONTACT_SETTING_ID)
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_deleteContactSetting() {

		assertThat(delegateRepository.findByPrincipalId(CONTACT_SETTING_ID)).hasSize(1);
		assertThat(delegateRepository.findByAgentId(CONTACT_SETTING_ID)).hasSize(1);
		assertThat(contactSettingRepository.findByCreatedById(CONTACT_SETTING_ID)).hasSize(1);

		setupCall()
			.withServicePath(PATH + "/" + CONTACT_SETTING_ID)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		// All related delegates and "virtual" child instances should have been removed.
		assertThat(delegateRepository.findByPrincipalId(CONTACT_SETTING_ID)).isEmpty();
		assertThat(delegateRepository.findByAgentId(CONTACT_SETTING_ID)).isEmpty();
		assertThat(contactSettingRepository.findByCreatedById(CONTACT_SETTING_ID)).isEmpty();
	}

	@Test
	void test05_findContactSettingsWithoutQueryFilter() {
		setupCall()
			.withServicePath(PATH + "?partyId=" + PARTY_ID)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_findContactSettingsWithQueryFilterAndMatch() {
		setupCall()
			.withServicePath(PATH + "?partyId=" + PARTY_ID + "&caseId=789")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_findContactSettingsWithQueryFilterAndNoMatch() {
		setupCall()
			.withServicePath(PATH + "?partyId=" + PARTY_ID + "&key=no-match")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_readChildren() {
		setupCall()
			.withServicePath(PATH + "/" + CONTACT_SETTING_ID + "/children")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_findByContactChannel() {
		setupCall()
			.withServicePath(PATH + "/contact-channels?destination=mr.pink@example.com")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
