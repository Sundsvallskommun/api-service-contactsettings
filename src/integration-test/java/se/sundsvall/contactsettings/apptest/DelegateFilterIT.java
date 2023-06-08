package se.sundsvall.contactsettings.apptest;

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
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.contactsettings.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/DelegateFilterIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class DelegateFilterIT extends AbstractAppTest {

	private static final String DELEGATE_ID = "7d5fbffc-d1ff-4fff-86de-8158b4e34459"; // "Mr Blue delegates to Mr Pink" in testdata-it.sql
	private static final String DELEGATE_FILTER_ID = "a28c428b-a374-417e-89ea-3dba7d30a2e9"; // "Mr Blue delegates to Mr Pink" in testdata-it.sql
	private static final String PATH = "/delegates/" + DELEGATE_ID + "/filters";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_createDelegateFilter() {
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
	void test02_readDelegateFilter() {
		setupCall()
			.withServicePath(PATH + "/" + DELEGATE_FILTER_ID)
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_updateDelegateFilter() {
		setupCall()
			.withServicePath(PATH + "/" + DELEGATE_FILTER_ID)
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_deleteDelegateFilter() {
		setupCall()
			.withServicePath(PATH + "/" + DELEGATE_FILTER_ID)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}
}
