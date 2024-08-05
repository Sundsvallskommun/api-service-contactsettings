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
import se.sundsvall.contactsettings.integration.db.DelegateFilterRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
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

	private static final String DELEGATE_ID_TO_DELETE = "ce4c877c-3b86-497f-84dd-24487ea4d396"; // "Mr Green delegates to Mr Brown" in testdata-it.sql
	private static final String DELEGATE_FILTER_ID_TO_DELETE_1 = "daaea12d-5643-4f2d-b9a6-bf15d836fcf5"; // "Mr Green delegates to Mr Brown" in testdata-it.sql
	private static final String DELEGATE_FILTER_ID_TO_DELETE_2 = "1d525a22-c805-494a-acaf-4961728d3a5c"; // "Mr Green delegates to Mr Brown" in testdata-it.sql

	private static final String PATH = "/2281/delegates/" + DELEGATE_ID + "/filters";
	private static final String DELETE_PATH = "/2281/delegates/" + DELEGATE_ID_TO_DELETE + "/filters";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Autowired
	private DelegateRepository delegateRepository;

	@Autowired
	private DelegateFilterRepository delegateFilterRepository;

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
	void test04_deleteDelegateFilterWhenNoMoreFiltersExist() {

		// Assumptions: One delegate with two filters exist.

		// Assert that both delegate and delegateFilter exists.
		assertThat(delegateRepository.existsById(DELEGATE_ID_TO_DELETE)).isTrue();
		assertThat(delegateFilterRepository.existsById(DELEGATE_FILTER_ID_TO_DELETE_1)).isTrue();
		assertThat(delegateFilterRepository.existsById(DELEGATE_FILTER_ID_TO_DELETE_2)).isTrue();

		// Delete first filter
		setupCall()
			.withServicePath(DELETE_PATH + "/" + DELEGATE_FILTER_ID_TO_DELETE_1)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		// Assert that only the first delegateFilter was deleted.
		assertThat(delegateRepository.existsById(DELEGATE_ID_TO_DELETE)).isTrue();
		assertThat(delegateFilterRepository.existsById(DELEGATE_FILTER_ID_TO_DELETE_1)).isFalse();
		assertThat(delegateFilterRepository.existsById(DELEGATE_FILTER_ID_TO_DELETE_2)).isTrue();

		// Delete second filter
		setupCall()
			.withServicePath(DELETE_PATH + "/" + DELEGATE_FILTER_ID_TO_DELETE_2)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		// Assert that both delegate and the second delegateFilter was deleted.
		assertThat(delegateRepository.existsById(DELEGATE_ID_TO_DELETE)).isFalse();
		assertThat(delegateFilterRepository.existsById(DELEGATE_FILTER_ID_TO_DELETE_1)).isFalse();
		assertThat(delegateFilterRepository.existsById(DELEGATE_FILTER_ID_TO_DELETE_2)).isFalse();
	}
}
