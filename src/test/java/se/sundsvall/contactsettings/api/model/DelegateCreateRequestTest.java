package se.sundsvall.contactsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;

import java.util.List;
import org.junit.jupiter.api.Test;

class DelegateCreateRequestTest {

	@Test
	void testBean() {
		assertThat(DelegateCreateRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var agentId = "agentId";
		final var filters = List.of(Filter.create()
			.withAlias("filter")
			.withRules(List.of(Rule.create().withAttributeName("attribute").withOperator(EQUALS).withAttributeValue("value"))));
		final var principalId = "principalId";

		final var bean = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withFilters(filters)
			.withPrincipalId(principalId);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAgentId()).isEqualTo(agentId);
		assertThat(bean.getFilters()).isEqualTo(filters);
		assertThat(bean.getPrincipalId()).isEqualTo(principalId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(DelegateCreateRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new DelegateCreateRequest()).hasAllNullFieldsOrProperties();
	}
}
