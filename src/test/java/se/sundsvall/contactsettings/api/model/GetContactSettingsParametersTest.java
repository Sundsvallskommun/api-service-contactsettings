package se.sundsvall.contactsettings.api.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class GetContactSettingsParametersTest {
	@Test
	void testBean() {
		assertThat(GetContactSettingsParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var partyId = "partyId";
		final var filterKey = "filterKey";
		final var filter = Map.of(filterKey,List.of("filter1", "filter2"));

		final var bean = GetContactSettingsParameters.create()
			.withPartyId(partyId)
			.withFilter(filter);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getFilter()).isEqualTo(filter);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(GetContactSettingsParameters.create()).hasAllNullFieldsOrProperties();
		assertThat(new GetContactSettingsParameters()).hasAllNullFieldsOrProperties();
	}
}
